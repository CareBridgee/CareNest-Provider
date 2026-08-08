package com.carenest.request.domain.repository

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<Request>
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
    suspend fun fetchRequestContract(requestId: String): Offer
    suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String,
    ): Boolean
    suspend fun acceptOffer(offerId: String): Boolean
    suspend fun completeRequest(requestId: String, visitCode: String): Boolean
}
