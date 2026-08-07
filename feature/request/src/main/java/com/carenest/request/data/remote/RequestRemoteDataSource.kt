package com.carenest.request.data.remote

import com.carenest.request.data.remote.dto.NurseOfferDto
import com.carenest.request.data.remote.dto.PatientAddressDto
import com.carenest.request.data.remote.dto.PatientProfileDto
import com.carenest.request.data.remote.dto.PatientReportDto
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.data.remote.dto.VisitCodeDto
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import javax.inject.Inject

interface RequestRemoteDataSource {
    suspend fun getServiceRequestDetails(serviceRequestId: String): ServiceRequestDetailsDto
    suspend fun getOffer(offerId: String): NurseOfferDto
    suspend fun acceptOffer(offerId: String): NurseOfferDto
    suspend fun cancelServiceRequest(serviceRequestId: String)
    suspend fun generateVisitCode(serviceRequestId: String): VisitCodeDto
    suspend fun completeServiceRequest(serviceRequestId: String, visitCode: String)
    suspend fun getPatientProfile(profileId: String): PatientProfileDto
    suspend fun getPatientReport(profileId: String): PatientReportDto
    suspend fun getPatientAddress(profileId: String): PatientAddressDto
}

class KtorRequestRemoteDataSource @Inject constructor(
    private val api: RequestApi,
) : RequestRemoteDataSource {
    override suspend fun getServiceRequestDetails(serviceRequestId: String): ServiceRequestDetailsDto =
        api.getServiceRequestDetails(serviceRequestId).bodyOrThrow()

    override suspend fun getOffer(offerId: String): NurseOfferDto =
        api.getOffer(offerId).bodyOrThrow()

    override suspend fun acceptOffer(offerId: String): NurseOfferDto =
        api.acceptOffer(offerId).bodyOrThrow()

    override suspend fun cancelServiceRequest(serviceRequestId: String) {
        api.cancelServiceRequest(serviceRequestId).requireSuccess()
    }

    override suspend fun generateVisitCode(serviceRequestId: String): VisitCodeDto =
        api.generateVisitCode(serviceRequestId).bodyOrThrow()

    override suspend fun completeServiceRequest(serviceRequestId: String, visitCode: String) {
        api.completeServiceRequest(serviceRequestId, visitCode).requireSuccess()
    }

    override suspend fun getPatientProfile(profileId: String): PatientProfileDto =
        api.getPatientProfile(profileId).bodyOrThrow()

    override suspend fun getPatientReport(profileId: String): PatientReportDto =
        api.getPatientReport(profileId).bodyOrThrow()

    override suspend fun getPatientAddress(profileId: String): PatientAddressDto =
        api.getPatientAddress(profileId).bodyOrThrow()
}

private suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    requireSuccess()
    return body()
}

private suspend fun HttpResponse.requireSuccess() {
    if (status.isSuccess()) return
    val errorBody = runCatching { bodyAsText() }.getOrDefault("")
    val message = errorBody.takeIf(String::isNotBlank)
        ?: "HTTP ${status.value} (${status.description})"
    throw IllegalStateException(message)
}
