package com.carenest.request.data.repository

import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.data.remote.RequestRemoteDataSource
import com.carenest.request.data.remote.dto.AddressSummaryDto
import com.carenest.request.data.remote.dto.NurseOfferDto
import com.carenest.request.data.remote.dto.PatientMedicalSummaryDto
import com.carenest.request.data.remote.dto.PatientReportDto
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.data.remote.dto.ServiceRequestNursePreviewDto
import com.carenest.request.data.remote.dto.ServiceRequestNurseProfileDto
import com.carenest.request.data.remote.dto.ServiceTypeSummaryDto
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NurseRequestsRepositoryImplTest {

    @Test
    fun `uses assigned profile and details for confirmed contract`() = runBlocking {
        val remote = FakeRequestRemoteDataSource().apply {
            details = {
                ServiceRequestDetailsDto(
                    serviceRequestId = "request-id",
                    serviceType = ServiceTypeSummaryDto(
                        name = "Wound care",
                        estimatedDurationMinutes = 45,
                    ),
                    distanceKm = 8.0,
                    offers = listOf(
                        NurseOfferDto(
                            id = "offer-id",
                            status = "ACCEPTED",
                            proposedPrice = 120.0,
                        ),
                    ),
                )
            }
            profile = {
                ServiceRequestNurseProfileDto(
                    serviceRequestId = "request-id",
                    preferredDate = "2026-08-10",
                    preferredTime = JsonPrimitive("09:30:00"),
                    status = "ACCEPTED",
                    patient = PatientMedicalSummaryDto(
                        profileId = "profile-id",
                        firstName = "Amina",
                        lastName = "Hassan",
                        profileImageUrl = "https://example.com/patient.jpg",
                    ),
                    patientPhoneNumber = "+201000000000",
                    address = AddressSummaryDto(city = "Giza"),
                )
            }
            report = { PatientReportDto(profileId = it, report = "Patient report") }
        }

        val result = repository(remote).fetchRequestContract("request-id")

        assertEquals(0, remote.previewCalls)
        assertEquals("https://example.com/patient.jpg", result.patientInfo.image)
        assertEquals("Patient report", result.patientInfo.summery)
        assertEquals("45 mins", result.estimatedDuration)
        assertEquals(4.97f, result.distanceMiles ?: 0f, 0.01f)
        assertEquals(120f, result.totalAmount)
    }

    @Test
    fun `falls back to preview for an unassigned request`() = runBlocking {
        val remote = FakeRequestRemoteDataSource().apply {
            details = { error("Not a participant") }
            profile = { error("Request is not assigned") }
            preview = {
                ServiceRequestNursePreviewDto(
                    serviceRequestId = "request-id",
                    serviceName = "IV therapy",
                    serviceDescription = "Monitor the infusion",
                    preferredDate = "2026-08-11",
                    preferredTime = JsonPrimitive("13:15:00"),
                    status = "SEARCHING",
                    estimatedPrice = 85.0,
                    patient = PatientMedicalSummaryDto(
                        profileId = "profile-id",
                        firstName = "Mona",
                        lastName = "Ali",
                        profileImageUrl = "https://example.com/preview.jpg",
                    ),
                )
            }
            report = { error("Report is unavailable before assignment") }
        }

        val result = repository(remote).fetchRequestContract("request-id")

        assertEquals(1, remote.previewCalls)
        assertEquals("Mona Ali", result.patientInfo.name)
        assertEquals("https://example.com/preview.jpg", result.patientInfo.image)
        assertEquals("Monitor the infusion", result.patientInfo.summery)
        assertEquals("IV therapy", result.serviceType)
        assertEquals("13:15", result.visitTime)
        assertEquals(85f, result.totalAmount)
        assertNull(result.distanceMiles)
        assertEquals("\u2014", result.estimatedDuration)
    }

    private fun repository(remote: RequestRemoteDataSource) = NurseRequestsRepositoryImpl(
        dataSource = NoOpNurseRequestsDataSource,
        remoteDataSource = remote,
    )
}

private object NoOpNurseRequestsDataSource : NurseRequestsDataSource {
    override suspend fun getIncomingRequests(): List<Request> = emptyList()
    override suspend fun getRequestContract(requestId: String): Offer = error("Not implemented")
    override suspend fun cancelRequest(requestId: String, reason: CancellationReason, note: String): Boolean = true
    override suspend fun completeRequest(serviceRequestId: String, visitCode: String): Boolean = true
    override suspend fun createOffer(requestId: String, proposedPrice: Double, message: String?) {}
    override suspend fun withdrawOffer(offerId: String) {}
    override fun listenReservationEvents(reservationId: String): Flow<ReservationEvent> = emptyFlow()
}

private class FakeRequestRemoteDataSource : RequestRemoteDataSource {
    var details: suspend () -> ServiceRequestDetailsDto = { error("Not configured") }
    var preview: suspend () -> ServiceRequestNursePreviewDto = { error("Not configured") }
    var profile: suspend () -> ServiceRequestNurseProfileDto = { error("Not configured") }
    var report: suspend (String) -> PatientReportDto = { error("Not configured") }
    var previewCalls = 0

    override suspend fun getServiceRequestDetails(serviceRequestId: String) = details()

    override suspend fun getServiceRequestPreview(
        serviceRequestId: String,
    ): ServiceRequestNursePreviewDto {
        previewCalls++
        return preview()
    }

    override suspend fun getServiceRequestProfile(serviceRequestId: String) = profile()

    override suspend fun getPatientReport(profileId: String) = report(profileId)

    override suspend fun acceptOffer(offerId: String): NurseOfferDto = error("Not used")

    override suspend fun cancelServiceRequest(serviceRequestId: String) = error("Not used")

    override suspend fun completeServiceRequest(serviceRequestId: String, visitCode: String) =
        error("Not used")
}
