package com.carenest.provider.auth.domain.model

sealed interface GoogleLoginResult {
    data class Authenticated(val nurse: AuthenticatedNurse?) : GoogleLoginResult
    data class PhoneRequired(
        val pendingToken: String,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val profileImageUrl: String? = null,
    ) : GoogleLoginResult
}