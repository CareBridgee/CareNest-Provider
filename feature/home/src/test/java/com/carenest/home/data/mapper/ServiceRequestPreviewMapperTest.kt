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
              "serviceType": {
                "id": "service-id",
                "name": "Home care",
                "imageUrl": "https://example.com/service.jpg"
              },
              "patient": {
                "firstName": "  Mona ",
                "lastName": " Ali  ",
                "profileImageUrl": "https://example.com/patient.jpg"
              }
            }
            """.trimIndent(),
        )

        val preview = dto.toDomain()
        val patient = preview.patient

        assertEquals("Mona Ali", patient?.fullName)
        assertEquals("https://example.com/patient.jpg", patient?.profileImageUrl)
        assertEquals("service-id", preview.serviceTypeId)
        assertEquals("Home care", preview.serviceName)
        assertEquals("https://example.com/service.jpg", preview.serviceImageUrl)
    }
}
