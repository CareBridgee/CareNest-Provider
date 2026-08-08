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

        val assignedProfileDeferred = async {
            runCatching { remoteDataSource.getServiceRequestProfile(requestId) }.getOrNull()
        }
        val nearbyOffersDeferred = async {
            runCatching { remoteDataSource.getNearbyOffers(requestId) }.getOrDefault(emptyList())
        }
        val detailsProfileId = details.profile?.id
        val reportDeferred = detailsProfileId?.let { profileId ->
            async {
                runCatching { remoteDataSource.getPatientReport(profileId) }.getOrNull()
            }
        }
        val assignedProfile = assignedProfileDeferred.await()
        val nearbyOffers = nearbyOffersDeferred.await()
        val acceptedOfferWithDistance = embeddedAcceptedOffer?.id?.let { acceptedOfferId ->
            nearbyOffers.firstOrNull { it.id == acceptedOfferId }
        } ?: nearbyOffers.firstOrNull {
            it.status.equals("ACCEPTED", ignoreCase = true)
        } ?: embeddedAcceptedOffer
        val report = reportDeferred?.await() ?: assignedProfile?.patient?.profileId?.let { profileId ->
            runCatching { remoteDataSource.getPatientReport(profileId) }.getOrNull()
        }

        details.toDomainOffer(
            requestedServiceRequestId = requestId,
            acceptedOffer = acceptedOfferWithDistance,
            assignedProfile = assignedProfile,
            patientReport = report,
        )
    }

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String,
    ): Boolean {
        // The backend cancellation operation has no request body yet. Keep the selected
        // reason and note in presentation state so the existing UI remains functional.
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
