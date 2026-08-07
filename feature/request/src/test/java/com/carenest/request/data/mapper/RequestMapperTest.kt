package com.carenest.request.data.mapper

import com.carenest.request.data.remote.dto.AddressSummaryDto
import com.carenest.request.data.remote.dto.NurseOfferDto
import com.carenest.request.data.remote.dto.NurseSummaryDto
import com.carenest.request.data.remote.dto.PatientMedicalSummaryDto
import com.carenest.request.data.remote.dto.PatientReportDto
import com.carenest.request.data.remote.dto.ProfileSummaryDto
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.data.remote.dto.ServiceRequestNurseProfileDto
import com.carenest.request.data.remote.dto.ServiceTypeSummaryDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class RequestMapperTest {
    @Test
    fun `maps contract data without inventing unavailable backend fields`() {
        val details = ServiceRequestDetailsDto(
            serviceRequestId = "request-id",
            serviceType = ServiceTypeSummaryDto(name = "Wound care", basePrice = 80.0),
            profile = ProfileSummaryDto(
                id = "profile-id",
                firstName = "Fallback",
                lastName = "Name",
                phoneNumber = "+201000000000",
            ),
            preferredDate = "2026-08-09",
            preferredTime = Json.parseToJsonElement("{\"hour\":14,\"minute\":30}"),
            durationMinutes = 45,
        )
        val offer = NurseOfferDto(
            id = "offer-id",
            proposedPrice = 95.0,
            proposedDate = "2026-08-10",
            proposedTime = Json.parseToJsonElement("\"15:45:00\""),
            status = "ACCEPTED",
        )

        val result = details.toDomainOffer(
            requestedServiceRequestId = "fallback-id",
            acceptedOffer = offer,
            assignedProfile = ServiceRequestNurseProfileDto(
                patient = PatientMedicalSummaryDto(
                    profileId = "profile-id",
                    firstName = "Amina",
                    lastName = "Hassan",
                    dateOfBirth = "2000-01-01",
                ),
                patientPhoneNumber = "+201111111111",
                address = AddressSummaryDto(
                    buildingNumber = "12",
                    street = "Nile Street",
                    apartmentNumber = "4B",
                    area = "Dokki",
                    city = "Giza",
                    country = "Egypt",
                ),
            ),
            patientReport = PatientReportDto(report = "Patient report"),
        )

        assertEquals("request-id", result.offerId)
        assertEquals("offer-id", result.nurseOfferId)
        assertEquals(null, result.reservationId)
        assertEquals("Amina Hassan", result.patientInfo.name)
        assertEquals("+201111111111", result.patientInfo.phone)
        assertEquals("Patient report", result.patientInfo.summery)
        assertEquals("15:45", result.visitTime)
        assertEquals("12 Nile Street", result.patientInfo.addressLine)
        assertEquals("4B, Dokki, Giza, Egypt", result.patientInfo.addressDetail)
        assertEquals("", result.patientInfo.image)
        assertEquals(null, result.distanceMiles)
        assertEquals(95f, result.totalAmount)
    }

    @Test
    fun `maps completed visit summary from backend service request`() {
        val details = ServiceRequestDetailsDto(
            serviceRequestId = "request-id",
            serviceType = ServiceTypeSummaryDto(name = "Wound care", basePrice = 80.0),
            nurse = NurseSummaryDto(firstName = "Sara", lastName = "Ali"),
            durationMinutes = 60,
            status = "COMPLETED",
            updatedAt = "2026-08-07T17:30:00Z",
        )

        val result = details.toVisitSummary(
            requestedServiceRequestId = "fallback-id",
            acceptedOffer = NurseOfferDto(proposedPrice = 95.0, status = "ACCEPTED"),
        )

        assertEquals("request-id", result.requestId)
        assertEquals("Sara Ali", result.professionalName)
        assertEquals("Wound care", result.serviceType)
        assertEquals(60, result.durationMinutes)
        assertEquals("2026-08-07", result.completedDate)
        assertEquals(95.0, result.totalAmount)
        assertEquals(true, result.isVerified)
    }
}
