package com.carenest.provider.auth.domain.model


data class AuthenticatedNurse(
    val id: String,
    val verificationStatus: NurseVerificationStatus,
    val hasSubmittedApplication: Boolean = false,
)