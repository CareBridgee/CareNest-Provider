package com.carenest.provider.core.network.socket.stomp

import android.util.Log
import com.carenest.provider.core.network.CredentialRejection
import com.carenest.provider.core.network.socket.model.SocketConnectionState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

internal enum class StompConnectResult {
    CONNECTED,
    AUTHENTICATION_FAILED,
    FAILED,
}

internal fun String?.indicatesSocketAuthenticationFailure(): Boolean =
    CredentialRejection.matchesText(this)

@Singleton
class StompClient @Inject constructor(
    private val httpClient: HttpClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow<SocketConnectionState>(SocketConnectionState.Disconnected)
    val connectionState: StateFlow<SocketConnectionState> = _connectionState.asStateFlow()

    private val _incomingFrames = MutableSharedFlow<StompFrame>(extraBufferCapacity = 64)
    val incomingFrames: SharedFlow<StompFrame> = _incomingFrames.asSharedFlow()

    private var session: DefaultClientWebSocketSession? = null
    private var readJob: Job? = null

    private val sendMutex = Mutex()
    private val subIdCounter = AtomicInteger(1)

    internal suspend fun connect(wsUrl: String, accessToken: String): StompConnectResult {
        if (_connectionState.value == SocketConnectionState.Connected) {
            Log.d("StompClient", "Already connected to $wsUrl")
            return StompConnectResult.CONNECTED
        }

        Log.i("StompClient", "Connecting to $wsUrl...")
        _connectionState.value = SocketConnectionState.Connecting

        try {
            val socketSession = httpClient.webSocketSession {
                url(wsUrl)
                header("Authorization", "Bearer $accessToken")
            }
            session = socketSession

            // Send CONNECT frame
            val connectFrame = StompFrame(
                command = StompCommand.CONNECT,
                headers = mapOf(
                    "Authorization" to "Bearer $accessToken",
                    "accept-version" to "1.2,1.1,1.0",
                    "heart-beat" to "10000,10000"
                )
            )

            Log.d("StompClient", "Sending STOMP CONNECT frame")
            sendFrameDirect(connectFrame)

            // Start listening loop and wait for CONNECTED
            val connectedChannel = Channel<StompConnectResult>(1)

            readJob?.cancel()
            readJob = scope.launch {
                listenIncomingFrames(socketSession, connectedChannel)
            }

            val result = connectedChannel.receiveCatching().getOrNull()
                ?: StompConnectResult.FAILED
            if (result == StompConnectResult.CONNECTED) {
                Log.i("StompClient", "STOMP Handshake SUCCESSFUL")
                _connectionState.value = SocketConnectionState.Connected
                return StompConnectResult.CONNECTED
            } else {
                Log.e("StompClient", "STOMP Handshake FAILED")
                disconnectInternal("Failed STOMP CONNECT handshake")
                return result
            }
        } catch (e: Exception) {
            Log.e("StompClient", "Connection attempt FAILED: ${e.message}", e)
            _connectionState.value = SocketConnectionState.Error(
                message = e.localizedMessage ?: "Failed to connect to socket",
                cause = e
            )
            disconnectInternal(e.localizedMessage ?: "Connection error")
            return if (e.message.indicatesSocketAuthenticationFailure()) {
                StompConnectResult.AUTHENTICATION_FAILED
            } else {
                StompConnectResult.FAILED
            }
        }
    }

    suspend fun send(destination: String, body: String? = null, headers: Map<String, String> = emptyMap()): Boolean {
        val sendHeaders = headers.toMutableMap()
        sendHeaders["destination"] = destination
        if (body != null && !sendHeaders.containsKey("content-type")) {
            sendHeaders["content-type"] = "application/json"
        }
        val frame = StompFrame(
            command = StompCommand.SEND,
            headers = sendHeaders,
            body = body
        )
        return sendFrameDirect(frame)
    }

    suspend fun subscribe(destination: String, customId: String? = null): String {
        val subId = customId ?: "sub-${subIdCounter.getAndIncrement()}"
        val frame = StompFrame(
            command = StompCommand.SUBSCRIBE,
            headers = mapOf(
                "id" to subId,
                "destination" to destination
            )
        )
        sendFrameDirect(frame)
        return subId
    }

    suspend fun unsubscribe(subscriptionId: String) {
        val frame = StompFrame(
            command = StompCommand.UNSUBSCRIBE,
            headers = mapOf("id" to subscriptionId)
        )
        sendFrameDirect(frame)
    }

    suspend fun disconnect() {
        if (_connectionState.value == SocketConnectionState.Disconnected) return
        Log.i("StompClient", "Disconnecting per user request")
        try {
            sendFrameDirect(StompFrame(command = StompCommand.DISCONNECT))
        } catch (_: Exception) { }
        disconnectInternal("User disconnected")
    }

    private suspend fun sendFrameDirect(frame: StompFrame): Boolean {
        val currentSession = session ?: return false
        return try {
            sendMutex.withLock {
                val raw = frame.encode()
                currentSession.send(Frame.Text(raw))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun listenIncomingFrames(
        socketSession: DefaultClientWebSocketSession,
        connectedChannel: Channel<StompConnectResult>
    ) {
        var handshakeCompleted = false

        try {
            socketSession.incoming.consumeAsFlow().collect { frame ->
                if (frame is Frame.Text) {
                    val rawText = frame.readText()
                    val stompFrames = StompFrame.decodeAll(rawText)

                    stompFrames.forEach { stompFrame ->
                        if (!handshakeCompleted) {
                            if (stompFrame.command == StompCommand.CONNECTED) {
                                handshakeCompleted = true
                                connectedChannel.trySend(StompConnectResult.CONNECTED)
                            } else if (stompFrame.command == StompCommand.ERROR) {
                                val errorMessage = listOfNotNull(
                                    stompFrame.headers["message"],
                                    stompFrame.body,
                                ).joinToString(" ")
                                val result = if (errorMessage.indicatesSocketAuthenticationFailure()) {
                                    StompConnectResult.AUTHENTICATION_FAILED
                                } else {
                                    StompConnectResult.FAILED
                                }
                                connectedChannel.trySend(result)
                                return@forEach
                            }
                        }

                        if (stompFrame.command == StompCommand.ERROR) {
                            // Server ERROR frame is terminal
                            _incomingFrames.emit(stompFrame)
                            _connectionState.value = SocketConnectionState.Error(
                                message = stompFrame.headers["message"] ?: stompFrame.body ?: "Server ERROR frame received"
                            )
                            disconnectInternal("Server terminal ERROR frame: ${stompFrame.headers["message"]}")
                        } else {
                            _incomingFrames.emit(stompFrame)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (_connectionState.value != SocketConnectionState.Disconnected) {
                _connectionState.value = SocketConnectionState.Error(
                    message = e.localizedMessage ?: "WebSocket incoming stream closed unexpectedly",
                    cause = e
                )
            }
        } finally {
            if (!handshakeCompleted) {
                connectedChannel.trySend(StompConnectResult.FAILED)
            }
            disconnectInternal("Socket stream finished")
        }
    }

    private suspend fun disconnectInternal(reason: String) {
        Log.i("StompClient", "Disconnecting internal. Reason: $reason")
        sendMutex.withLock {
            try {
                session?.close()
            } catch (_: Exception) { }
            session = null
        }
        readJob?.cancel()
        readJob = null

        if (_connectionState.value !is SocketConnectionState.Error) {
            _connectionState.value = SocketConnectionState.Disconnected
        }
    }
}
