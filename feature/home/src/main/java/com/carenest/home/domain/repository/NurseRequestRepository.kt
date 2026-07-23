package com.carenest.home.domain.repository

import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<NurseRequest>
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
    suspend fun fetchEarningsSummary(): EarningsSummary
    suspend fun getNurseProfile(): NurseProfile
}