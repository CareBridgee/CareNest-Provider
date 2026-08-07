package com.carenest.request.data.datasource

import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.RequestStatus
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeNurseRequestsDataSource @Inject constructor() : NurseRequestsDataSource {

    override suspend fun getIncomingRequests(): List<Request> {
        delay(1000)
        return listOf(
            Request(
                id = "req-001",
                patientName = "Sarah Mitchell",
                serviceName = "Post-Surgery Wound Care",
                basePrice = 45f,
                patientAddress = "2.4 mi",
                serviceImage = "",
                status = RequestStatus.ESTIMATED
            ),
            Request(
                id = "req-002",
                patientName = "James Okonkwo",
                serviceName = "IV Therapy & Monitoring",
                basePrice = 55f,
                patientAddress = "4.1 mi",
                serviceImage = "",
                status = RequestStatus.ESTIMATED
            )
        )
    }

    override fun sendOfferToPatient(requestId: String): Pair<Boolean, Int> {
        val random = Random(requestId.hashCode())
        val willAccept = random.nextInt(3) != 0
        val acceptAtSecond = if (willAccept) random.nextInt(4, 9) else 10
        return willAccept to acceptAtSecond
    }

}
