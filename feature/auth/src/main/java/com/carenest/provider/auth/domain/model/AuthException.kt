package com.carenest.provider.auth.domain.model

enum class AuthFailure {
    Network,
    InvalidPhone,
    InvalidOtp,
    ExpiredOtp,
    TooManyRequests,
    Server,
    Unknown,
}

class AuthException(
    val failure: AuthFailure,
    message: String,
    val statusCode: Int? = null,
    val backendCode: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
