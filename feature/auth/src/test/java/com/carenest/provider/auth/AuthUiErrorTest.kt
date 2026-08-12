package com.carenest.provider.auth

import com.carenest.provider.auth.domain.model.AuthException
import com.carenest.provider.auth.domain.model.AuthFailure
import com.carenest.provider.auth.presentation.auth.AuthUiError
import com.carenest.provider.auth.presentation.auth.toAuthUiError
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthUiErrorTest {

    @Test
    fun authFailuresMapToSpecificUserFacingErrors() {
        val cases = listOf(
            AuthFailure.Network to AuthUiError.NetworkUnavailable,
            AuthFailure.InvalidPhone to AuthUiError.InvalidPhone,
            AuthFailure.InvalidOtp to AuthUiError.InvalidOtp,
            AuthFailure.ExpiredOtp to AuthUiError.ExpiredOtp,
            AuthFailure.TooManyRequests to AuthUiError.TooManyRequests,
            AuthFailure.Server to AuthUiError.ServiceUnavailable,
            AuthFailure.Unknown to AuthUiError.VerificationFailed,
        )

        cases.forEach { (failure, expected) ->
            val error = AuthException(failure, "technical backend message")
            assertEquals(expected, error.toAuthUiError(AuthUiError.VerificationFailed))
        }
    }

    @Test
    fun ioErrorsMapToNetworkMessageWithoutExposingTechnicalDetails() {
        assertEquals(
            AuthUiError.NetworkUnavailable,
            IOException("socket details").toAuthUiError(AuthUiError.VerificationFailed),
        )
    }
}
