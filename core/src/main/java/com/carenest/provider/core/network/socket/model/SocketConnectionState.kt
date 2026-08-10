package com.carenest.provider.core.network.socket.model

sealed interface SocketConnectionState {
    data object Disconnected : SocketConnectionState
    data object Connecting : SocketConnectionState
    data object Connected : SocketConnectionState
    data class Reconnecting(val attempt: Int) : SocketConnectionState
    data class Error(val message: String, val cause: Throwable? = null) : SocketConnectionState
}
