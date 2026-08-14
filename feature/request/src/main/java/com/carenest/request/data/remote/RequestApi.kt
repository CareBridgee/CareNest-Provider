package com.carenest.request.data.remote

import com.carenest.request.data.remote.dto.CompleteServiceRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RequestApi @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun getServiceRequestDetails(serviceRequestId: String): HttpResponse =
        httpClient.get("/api/v1/service-requests/$serviceRequestId")

    suspend fun getServiceRequestPreview(serviceRequestId: String): HttpResponse =
        httpClient.get("/api/v1/service-requests/$serviceRequestId/preview")

    suspend fun acceptOffer(offerId: String): HttpResponse =
        httpClient.patch("/api/v1/nurse-offers/$offerId/accept")

    suspend fun cancelServiceRequest(serviceRequestId: String): HttpResponse =
        httpClient.patch("/api/v1/service-requests/$serviceRequestId/cancel")

    suspend fun completeServiceRequest(serviceRequestId: String, visitCode: String): HttpResponse =
        httpClient.post("/api/v1/service-requests/$serviceRequestId/complete") {
            setBody(CompleteServiceRequestDto(visitCode))
        }

    suspend fun getServiceRequestProfile(serviceRequestId: String): HttpResponse =
        httpClient.get("/api/v1/service-requests/$serviceRequestId/profile")

    suspend fun getNurseRequestHistory(): HttpResponse =
        httpClient.get("/api/v1/service-requests/nurse/history")

    suspend fun getPatientReport(profileId: String): HttpResponse =
        httpClient.get("/api/v1/profiles/report/$profileId/report")

}
