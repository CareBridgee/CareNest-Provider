package com.carenest.provider.auth

import com.carenest.provider.auth.data.remote.dto.GoogleLoginRequestDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleLoginRequestDtoTest {

    @Test
    fun serializesOnlyTheGoogleIdTokenRequiredByTheBackendContract() {
        val body = Json.encodeToString(GoogleLoginRequestDto(idToken = "google-id-token"))

        assertEquals("{\"idToken\":\"google-id-token\"}", body)
    }
}
