package com.carenest.request.data.repository

import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.data.mapper.toDomain
import com.carenest.request.data.mapper.toDomainOffer
import com.carenest.request.data.remote.RequestRemoteDataSource
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.VisitCode
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
        val details = remoteDataSource.getServiceRequestDetails(requestId)
        val embeddedAcceptedOffer = details.offers.firstOrNull {
            it.status.equals("ACCEPTED", ignoreCase = true)
        } ?: details.offers.singleOrNull()

        val offerDeferred = async {
            embeddedAcceptedOffer?.id?.let { offerId ->
                runCatching { remoteDataSource.getOffer(offerId) }.getOrNull()
            } ?: embeddedAcceptedOffer
        }

        val profileId = details.profile?.id
        val profileDeferred = async {
            profileId?.let { runCatching { remoteDataSource.getPatientProfile(it) }.getOrNull() }
        }
        val reportDeferred = async {
            profileId?.let { runCatching { remoteDataSource.getPatientReport(it) }.getOrNull() }
        }
        val addressDeferred = async {
            profileId?.let { runCatching { remoteDataSource.getPatientAddress(it) }.getOrNull() }
        }

        details.toDomainOffer(
            requestedServiceRequestId = requestId,
            acceptedOffer = offerDeferred.await(),
            patientProfile = profileDeferred.await(),
            patientReport = reportDeferred.await(),
            patientAddress = addressDeferred.await(),
        )
    }

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean {
        // The current OpenAPI operation has no cancellation request body, so reason/note
        // remain presentation-only until the backend contract supports them.
        remoteDataSource.cancelServiceRequest(requestId)
        return true
    }

    override suspend fun acceptOffer(offerId: String): Boolean {
        remoteDataSource.acceptOffer(offerId)
        return true
    }

    override suspend fun generateVisitCode(requestId: String): VisitCode =
        remoteDataSource.generateVisitCode(requestId).toDomain(requestId).also {
            check(it.code.isNotBlank()) { "The backend returned an empty visit code." }
        }

    override suspend fun completeRequest(requestId: String, visitCode: String): Boolean {
        remoteDataSource.completeServiceRequest(requestId, visitCode)
        return true
    }
}
