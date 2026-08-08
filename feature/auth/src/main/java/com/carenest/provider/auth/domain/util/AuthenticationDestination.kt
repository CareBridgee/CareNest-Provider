package com.carenest.provider.auth.domain.util

sealed interface AuthenticationDestination {
    data object CompleteProfile : AuthenticationDestination
    data class UnderReview(val nurseId: String) : AuthenticationDestination
    data class Approved(val nurseId: String) : AuthenticationDestination
    data class Rejected(val nurseId: String) : AuthenticationDestination
}