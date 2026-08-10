package com.carenest.provider.core.network.socket.stomp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class StompFrameTest {

    @Test
    fun `encode CONNECT frame produces valid STOMP format`() {
        val frame = StompFrame(
            command = StompCommand.CONNECT,
            headers = mapOf(
                "Authorization" to "Bearer testToken",
                "accept-version" to "1.2,1.1,1.0"
            )
        )

        val encoded = frame.encode()
        assertEquals(
            "CONNECT\nAuthorization:Bearer testToken\naccept-version:1.2,1.1,1.0\n\n\u0000",
            encoded
        )
    }

    @Test
    fun `decode MESSAGE frame parses headers and body correctly`() {
        val raw = "MESSAGE\ndestination:/user/queue/notifications\ncontent-type:application/json\n\n{\"title\":\"New Request\"}\u0000"

        val frames = StompFrame.decodeAll(raw)
        assertEquals(1, frames.size)
        val frame = frames[0]
        assertEquals(StompCommand.MESSAGE, frame.command)
        assertEquals("/user/queue/notifications", frame.headers["destination"])
        assertEquals("{\"title\":\"New Request\"}", frame.body)
    }

    @Test
    fun `decode frame with CRLF line endings handles whitespace cleanly`() {
        val raw = "CONNECTED\r\nversion:1.2\r\nheart-beat:10000,10000\r\n\r\n\u0000"

        val frames = StompFrame.decodeAll(raw)
        assertEquals(1, frames.size)
        val frame = frames[0]
        assertEquals(StompCommand.CONNECTED, frame.command)
        assertEquals("1.2", frame.headers["version"])
        assertEquals("10000,10000", frame.headers["heart-beat"])
        assertNull(frame.body)
    }

    @Test
    fun `decode multiple frames in one message works`() {
        val raw = "\n\nCONNECTED\nversion:1.2\n\n\u0000MESSAGE\ndestination:test\n\nbody\u0000"

        val frames = StompFrame.decodeAll(raw)
        assertEquals(2, frames.size)
        assertEquals(StompCommand.CONNECTED, frames[0].command)
        assertEquals(StompCommand.MESSAGE, frames[1].command)
        assertEquals("body", frames[1].body)
    }

    @Test
    fun `decode handles leading heartbeats`() {
        val raw = "\n\n\nMESSAGE\ndestination:test\n\nbody\u0000"
        val frames = StompFrame.decodeAll(raw)
        assertEquals(1, frames.size)
        assertEquals(StompCommand.MESSAGE, frames[0].command)
    }
}
