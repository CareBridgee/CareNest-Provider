package com.carenest.provider.auth.domain.usecase

import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        phoneNumber: String,
        otp: String,
        pendingToken: String? = null,
    ): Result<AuthenticatedNurse?> {
        return repository.verifyOtp(phoneNumber, otp, pendingToken)
    }
}
