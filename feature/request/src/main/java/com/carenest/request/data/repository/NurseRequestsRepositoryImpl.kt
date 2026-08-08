package com.carenest.request.data.repository

import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.data.mapper.toDomainOffer
import com.carenest.request.data.remote.RequestRemoteDataSource
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.repository.NurseRequestsRepository
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

    override fun sendOfferToPatient(requestId: String): Pair<Boolean, Int> =
        dataSource.sendOfferToPatient(requestId)

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
        }.getOrElse { previewError ->
            profileResult.exceptionOrNull()?.let(previewError::addSuppressed)
            throw previewError
        }
        val report = preview.patient?.profileId?.let { profileId ->
            runCatching { remoteDataSource.getPatientReport(profileId) }.getOrNull()
        }
        preview.toDomainOffer(
            requestedServiceRequestId = requestId,
            details = details,
            acceptedOffer = acceptedOffer,
            patientReport = report,
        )
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

    override suspend fun completeRequest(requestId: String, visitCode: String): Boolean {
        remoteDataSource.completeServiceRequest(requestId, visitCode)
        return true
    }
}

private fun ServiceRequestDetailsDto?.acceptedOffer() = this?.offers?.firstOrNull {
    it.status.equals("ACCEPTED", ignoreCase = true)
} ?: this?.offers?.singleOrNull()
