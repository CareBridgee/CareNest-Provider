package com.carenest.provider.core.network.socket.stomp

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StompSession(
    private val httpClient: HttpClient,
    private val json: Json,
    private val url: String,
    private val authToken: String
) {
    private var session: WebSocketSession? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _frames = MutableSharedFlow<StompFrame>()
    val frames: SharedFlow<StompFrame> = _frames.asSharedFlow()

    suspend fun connect() {
        session = httpClient.webSocketSession(url)
        
        val connectFrame = StompFrame(
            command = StompCommand.CONNECT,
            headers = mapOf(
                "Authorization" to "Bearer $authToken",
                "accept-version" to "1.2,1.1,1.0",
                "heart-beat" to "10000,10000"
            )
        )
        
        sendFrame(connectFrame)
        
        scope.launch {
            listen()
        }
    }

    private suspend fun listen() {
        session?.let { ws ->
            for (frame in ws.incoming) {
                if (frame is Frame.Text) {
                    val rawText = frame.readText()
                    StompFrame.decodeAll(rawText).forEach { stompFrame ->
                        _frames.emit(stompFrame)
                    }
                }
            }
        }
    }

    suspend fun subscribe(destination: String, id: String = destination) {
        val frame = StompFrame(
            command = StompCommand.SUBSCRIBE,
            headers = mapOf(
                "destination" to destination,
                "id" to id
            )
        )
        sendFrame(frame)
    }

    suspend fun send(destination: String, body: Any? = null) {
        val frame = StompFrame(
            command = StompCommand.SEND,
            headers = mapOf("destination" to destination),
            body = body?.let { if (it is String) it else json.encodeToString(it) }
        )
        sendFrame(frame)
    }

    private suspend fun sendFrame(frame: StompFrame) {
        session?.send(Frame.Text(frame.encode()))
    }

    fun subscribeToTopic(topic: String): Flow<String> = frames
        .filter { it.command == StompCommand.MESSAGE && it.headers["destination"] == topic }
        .mapNotNull { it.body }

    suspend fun disconnect() {
        if (session?.isActive == true) {
            sendFrame(StompFrame(StompCommand.DISCONNECT))
        }
        session = null
    }
}
