package com.carenest.provider.profile.domain.usecase.auth

import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor() {
    suspend operator fun invoke(phoneNumber: String, otp: String): Result<Unit> {
        // Stub implementation
        return Result.success(Unit)
    }
}
