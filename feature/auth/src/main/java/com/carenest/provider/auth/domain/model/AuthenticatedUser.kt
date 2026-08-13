package com.carenest.provider.auth.domain.model

data class AuthenticatedUser(
    val id: String? = null,
    val profileCompleted: Boolean,
    val nurse: AuthenticatedNurse? = null,
)