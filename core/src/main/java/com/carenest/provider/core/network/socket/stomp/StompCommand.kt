package com.carenest.provider.core.network.socket.stomp

enum class StompCommand {
    CONNECT,
    CONNECTED,
    SUBSCRIBE,
    UNSUBSCRIBE,
    SEND,
    MESSAGE,
    ERROR,
    RECEIPT,
    DISCONNECT
}
