package com.carenest.home.data.mapper

import com.carenest.home.data.dto.ServiceRequestPreviewDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ServiceRequestPreviewMapperTest {

    @Test
    fun `preview maps patient identity when optional medical fields are absent`() {
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<ServiceRequestPreviewDto>(
            """
            {
              "serviceRequestId": "request-id",
              "serviceName": "Home care",
              "patient": {
                "firstName": "  Mona ",
                "lastName": " Ali  ",
                "profileImageUrl": "https://example.com/patient.jpg"
              }
            }
            """.trimIndent(),
        )

        val patient = dto.toDomain().patient

        assertEquals("Mona Ali", patient?.fullName)
        assertEquals("https://example.com/patient.jpg", patient?.profileImageUrl)
    }
}
