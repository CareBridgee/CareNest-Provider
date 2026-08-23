package com.carenest.home.domain.usecase

import com.carenest.home.domain.repository.NurseRequestsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SendOfferToPatientUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    suspend operator fun invoke(
        requestId: String,
        proposedPrice: Double,
        proposedDate: String? = null,
        proposedTime: String? = null,
        message: String? = null
    ) {
        val currentDate = proposedDate?.takeIf(String::isNotBlank)
            ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val currentTime = proposedTime?.takeIf(String::isNotBlank)
            ?: SimpleDateFormat("HH:mm", Locale.US).format(Date())

        repository.createOffer(
            serviceRequestId = requestId,
            proposedPrice = proposedPrice,
            proposedDate = currentDate,
            proposedTime = currentTime,
            message = message
        )
    }
}