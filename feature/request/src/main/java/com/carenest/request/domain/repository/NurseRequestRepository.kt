package com.carenest.request.domain.repository

import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.VisitCode

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<Request>
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
    suspend fun fetchRequestContract(requestId: String): Offer
    suspend fun cancelRequest(requestId: String): Boolean
    suspend fun acceptOffer(offerId: String): Boolean
    suspend fun generateVisitCode(requestId: String): VisitCode
    suspend fun completeRequest(requestId: String, visitCode: String): Boolean
}
