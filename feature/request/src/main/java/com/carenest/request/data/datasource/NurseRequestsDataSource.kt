package com.carenest.request.data.datasource

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request

interface NurseRequestsDataSource {
    suspend fun getIncomingRequests(): List<Request>
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
    suspend fun getRequestContract(requestId: String): Offer
    suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean
}