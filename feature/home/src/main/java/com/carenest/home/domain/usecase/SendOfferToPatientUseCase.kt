package com.carenest.home.domain.usecase

import com.carenest.home.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class SendOfferToPatientUseCase @Inject constructor(
   private val  repository: NurseRequestsRepository
){
    operator fun invoke(requestId: String): Pair<Boolean, Int> =
        repository.sendOfferToPatient(requestId)
}