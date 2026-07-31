package com.carenest.request.data.datasource

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientInfo
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

    override suspend fun getRequestContract(requestId: String): Offer {
        delay(800)
        return Offer(
            offerId = requestId,
            visitDate = "Today, Nov 24",
            visitTime = "2:30 PM",
            distanceMiles = 2.4f,
            estimatedArrival = "10:30 AM",
            estimatedDuration = "45 mins",
            patientInfo = PatientInfo(
                id = "pat-001",
                name = "Sarah Mitchell",
                age = 72,
                image = "",
                phone = "+1 (310) 555-0142",
                addressLine = "1224 Oakwood Heights",
                addressDetail = "Apt 4B, Beverly Hills, CA 90210",
                summery = "Patient needs wound care post-surgery.",
                distanceMiles = 2.4f
            ),
            totalAmount = 95f,
            serviceType = "Wound Care",
            serviceImage = ""
        )
    }

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean {
        delay(500)
        return true
    }
}