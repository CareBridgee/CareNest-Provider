package com.carenest.provider.core.network

import io.ktor.http.HttpStatusCode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CredentialRejectionTest {

    @Test
    fun candidateStatusesCoverExactlyTheRejectionShapedCodes() {
        assertTrue(CredentialRejection.isCandidateStatus(HttpStatusCode.BadRequest))
        assertTrue(CredentialRejection.isCandidateStatus(HttpStatusCode.Unauthorized))
        assertTrue(CredentialRejection.isCandidateStatus(HttpStatusCode.Forbidden))
        assertTrue(CredentialRejection.isCandidateStatus(401))
    }

    @Test
    fun successRedirectNotFoundAndServerErrorsAreNeverRejections() {
        assertFalse(CredentialRejection.isCandidateStatus(HttpStatusCode.OK))
        assertFalse(CredentialRejection.isCandidateStatus(HttpStatusCode.MovedPermanently))
        assertFalse(CredentialRejection.isCandidateStatus(HttpStatusCode.NotFound))
        assertFalse(CredentialRejection.isCandidateStatus(HttpStatusCode.InternalServerError))
        assertFalse(CredentialRejection.isCandidateStatus(HttpStatusCode.ServiceUnavailable))
        assertFalse(CredentialRejection.isCandidateStatus(429))
    }

    @Test
    fun textMarkersMatchAuthenticationFailuresAcrossCases() {
        assertTrue(CredentialRejection.matchesText("Handshake failed with status code 401"))
        assertTrue(CredentialRejection.matchesText("Forbidden"))
        assertTrue(CredentialRejection.matchesText("EXPIRED JWT"))
        assertTrue(CredentialRejection.matchesText("Access Denied by server"))
        assertTrue(CredentialRejection.matchesText("authentication failed: bad credentials"))
        assertTrue(CredentialRejection.matchesText("Token is not authenticated"))
    }

    @Test
    fun genericInfrastructureAndBrokerMessagesAreNotMisclassified() {
        assertFalse(CredentialRejection.matchesText(null))
        assertFalse(CredentialRejection.matchesText(""))
        assertFalse(CredentialRejection.matchesText("Network is unreachable"))
        assertFalse(CredentialRejection.matchesText("Connection timed out"))
        assertFalse(CredentialRejection.matchesText("Server is temporarily unavailable"))
        // The bare word "authentication" must not match on its own.
        assertFalse(CredentialRejection.matchesText("Proxy Authentication Required"))
        assertFalse(CredentialRejection.matchesText("Authentication required for destination /topic/chat"))
    }
}
