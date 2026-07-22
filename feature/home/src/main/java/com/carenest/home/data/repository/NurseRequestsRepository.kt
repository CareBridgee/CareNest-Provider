package com.carenest.home.data.repository

import com.carenest.home.domain.model.NurseRequest
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<NurseRequest>
    fun simulatePatientAcceptance(requestId: String): Pair<Boolean, Int>
}

@Singleton
class FakeNurseRequestsRepository @Inject constructor() : NurseRequestsRepository {

    override suspend fun fetchIncomingRequests(): List<NurseRequest> {
        delay(FETCH_DELAY_MS)
        return listOf(
            NurseRequest(
                id = "req-001",
                patientName = "Sarah Mitchell",
                serviceType = "Post-Surgery Wound Care",
                baseRate = 45f,
                distanceMiles = 2.4f,
                patientImage = "",
                serviceImage = ""
            ),
            NurseRequest(
                id = "req-002",
                patientName = "James Okonkwo",
                serviceType = "IV Therapy & Monitoring",
                baseRate = 55f,
                distanceMiles = 4.1f,
                patientImage = "",
                serviceImage = ""
            ),
            NurseRequest(
                id = "req-003",
                patientName = "Emily Chen",
                serviceType = "Elderly Mobility Support",
                baseRate = 40f,
                distanceMiles = 1.8f,
                patientImage = "",
                serviceImage = ""
            ),
            NurseRequest(
                id = "req-004",
                patientName = "Robert Alvarez",
                serviceType = "Medication Administration",
                baseRate = 50f,
                distanceMiles = 5.6f,
                patientImage = "",
                serviceImage = ""
            ),
        )
    }

    override fun simulatePatientAcceptance(requestId: String): Pair<Boolean, Int> {
        val random = Random(requestId.hashCode())
        val willAccept = random.nextInt(3) != 0
        val acceptAtSecond = if (willAccept) random.nextInt(4, 9) else OFFER_TIMEOUT_SECONDS
        return willAccept to acceptAtSecond
    }

    private companion object {
        const val FETCH_DELAY_MS = 1_500L
        const val OFFER_TIMEOUT_SECONDS = 10
    }
}
