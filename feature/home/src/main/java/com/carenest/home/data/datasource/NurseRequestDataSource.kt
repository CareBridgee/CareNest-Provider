package com.carenest.home.data.datasource

import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest


interface NurseRequestsDataSource {
    suspend fun getIncomingRequests(): List<NurseRequest>
    suspend fun getEarningsSummary(): EarningsSummary
    fun sendOfferToPatient(requestId: String): Pair<Boolean, Int>
    suspend fun getNurseProfile(): NurseProfile
}