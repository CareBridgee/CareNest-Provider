package com.carenest.provider.core.network.socket.model

import org.junit.Assert.assertEquals
import org.junit.Test

class NearbyNurseServiceRequestResponseTest {

    @Test
    fun `patient display name combines and trims patient names`() {
        val response = response(
            patientFirstName = "  Mona ",
            patientLastName = " Ali  ",
        )

        assertEquals("Mona Ali", response.patientDisplayName())
    }

    @Test
    fun `patient display name uses fallback when names are missing`() {
        assertEquals("Patient Request", response().patientDisplayName())
    }

    private fun response(
        patientFirstName: String? = null,
        patientLastName: String? = null,
    ) = NearbyNurseServiceRequestResponse(
        serviceRequestId = "request-id",
        profileId = "profile-id",
        patientFirstName = patientFirstName,
        patientLastName = patientLastName,
        latitude = 30.0,
        longitude = 31.0,
    )
}
