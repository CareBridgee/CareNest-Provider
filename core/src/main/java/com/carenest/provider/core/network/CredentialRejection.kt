package com.carenest.provider.core.network

import io.ktor.http.HttpStatusCode

/**
 * Single source of truth for "did the server just tell us our credentials are dead?"
 *
 * Every surface that decides session fate from a server response - REST token
 * refresh, the WebSocket handshake, identity-endpoint 403 handling - derives its
 * answer from here so a backend semantic change lands in exactly one file.
 *
 * CareNest backends may answer 403 (not only 401) for expired or revoked JWTs,
 * so status codes alone cannot separate "token rejected" from "authorization
 * denied" or "infrastructure blocked us"; callers combine [candidateStatuses]
 * with their own confirmation policy before destroying anything.
 */
object CredentialRejection {

    /**
     * HTTP statuses that are consistent with credential rejection. Anything else
     * (timeouts, 5xx, 404...) must never be read as "credentials are dead".
     */
    val candidateStatuses: Set<Int> = setOf(400, 401, 403)

    fun isCandidateStatus(status: HttpStatusCode): Boolean = status.value in candidateStatuses

    fun isCandidateStatus(status: Int): Boolean = status in candidateStatuses

    /**
     * Free-form text markers consistent with an authentication failure. Kept
     * deliberately specific: broad entries such as the bare word "authentication"
     * match unrelated infrastructure ("Proxy Authentication Required") and broker
     * chatter, turning non-auth failures into refresh attempts.
     */
    private val textMarkers = listOf(
        "401",
        "403",
        "unauthorized",
        "forbidden",
        "access denied",
        "invalid token",
        "expired token",
        "invalid jwt",
        "expired jwt",
        "authentication failed",
        "not authenticated",
    )

    fun matchesText(message: String?): Boolean {
        val value = message?.lowercase() ?: return false
        return textMarkers.any(value::contains)
    }
}
