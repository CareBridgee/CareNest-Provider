package com.carenest.request.data.repository

import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.data.mapper.toDomainOffer
import com.carenest.request.data.remote.RequestRemoteDataSource
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.repository.NurseRequestsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Singleton
class NurseRequestsRepositoryImpl @Inject constructor(
    private val dataSource: NurseRequestsDataSource,
    private val remoteDataSource: RequestRemoteDataSource,
) : NurseRequestsRepository {

    override suspend fun fetchIncomingRequests(): List<Request> =
        dataSource.getIncomingRequests()

    override suspend fun fetchRequestContract(requestId: String): Offer = coroutineScope {
        val detailsDeferred = async {
            runCatching { remoteDataSource.getServiceRequestDetails(requestId) }.getOrNull()
        }
        val profileResult = runCatching {
            remoteDataSource.getServiceRequestProfile(requestId)
        }

        val details = detailsDeferred.await()
        val acceptedOffer = details.acceptedOffer()

        profileResult.getOrNull()?.let { profile ->
            val report = profile.patient?.profileId?.let { profileId ->
                runCatching { remoteDataSource.getPatientReport(profileId) }.getOrNull()
            }
            return@coroutineScope profile.toDomainOffer(
                requestedServiceRequestId = requestId,
                details = details,
                acceptedOffer = acceptedOffer,
                patientReport = report,
            )
        }

        val preview = runCatching {
            remoteDataSource.getServiceRequestPreview(requestId)
        }.getOrNull()

        if (preview != null) {
            val report = preview.patient?.profileId?.let { profileId ->
                runCatching { remoteDataSource.getPatientReport(profileId) }.getOrNull()
            }
            return@coroutineScope preview.toDomainOffer(
                requestedServiceRequestId = requestId,
                details = details,
                acceptedOffer = acceptedOffer,
                patientReport = report,
            )
        }

        if (details != null) {
            val report = details.profile?.id?.let { profileId ->
                runCatching { remoteDataSource.getPatientReport(profileId) }.getOrNull()
            }
            return@coroutineScope details.toDomainOffer(
                requestedServiceRequestId = requestId,
                acceptedOffer = acceptedOffer,
                assignedProfile = null,
                patientReport = report,
            )
        }

        throw profileResult.exceptionOrNull()
            ?: IllegalStateException("Could not load request contract for $requestId")
    }

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String,
    ): Boolean {
        remoteDataSource.cancelServiceRequest(requestId)
        return true
    }

    override suspend fun acceptOffer(offerId: String): Boolean {
        remoteDataSource.acceptOffer(offerId)
        return true
    }

    override suspend fun completeRequest(serviceRequestId: String, visitCode: String): Boolean {
        remoteDataSource.completeServiceRequest(serviceRequestId, visitCode)
        return true
    }

    override suspend fun createOffer(requestId: String, proposedPrice: Double, message: String?) {
        dataSource.createOffer(requestId, proposedPrice, message)
    }

    override suspend fun withdrawOffer(offerId: String) {
        dataSource.withdrawOffer(offerId)
    }

    override fun listenReservationEvents(reservationId: String): Flow<ReservationEvent> =
        dataSource.listenReservationEvents(reservationId)
}

private fun ServiceRequestDetailsDto?.acceptedOffer() = this?.offers?.firstOrNull {
    it.status.equals("ACCEPTED", ignoreCase = true)
} ?: this?.offers?.singleOrNull()
