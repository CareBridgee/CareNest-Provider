package com.carenest.request.domain.repository

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.NurseRequest
import com.carenest.request.domain.model.RequestContract

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<NurseRequest>
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
    suspend fun fetchRequestContract(requestId: String): RequestContract
    suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String,
    ): Boolean
}