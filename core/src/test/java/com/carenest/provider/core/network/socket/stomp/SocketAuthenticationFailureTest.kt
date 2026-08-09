package com.carenest.provider.core.network.socket.stomp

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SocketAuthenticationFailureTest {

    @Test
    fun authenticationFailuresAreRecognized() {
        assertTrue("Handshake failed with status code 401".indicatesSocketAuthenticationFailure())
        assertTrue("Forbidden".indicatesSocketAuthenticationFailure())
        assertTrue("Expired JWT".indicatesSocketAuthenticationFailure())
        assertTrue("Access denied".indicatesSocketAuthenticationFailure())
    }

    @Test
    fun ordinaryConnectionFailuresDoNotTriggerTokenRefresh() {
        assertFalse("Network is unreachable".indicatesSocketAuthenticationFailure())
        assertFalse("Connection timed out".indicatesSocketAuthenticationFailure())
        assertFalse("Server is temporarily unavailable".indicatesSocketAuthenticationFailure())
        assertFalse(null.indicatesSocketAuthenticationFailure())
    }
}
