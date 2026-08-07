package com.carenest.request.data.datasource

import com.carenest.request.domain.model.Request

interface NurseRequestsDataSource {
    suspend fun getIncomingRequests(): List<Request>
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
}
