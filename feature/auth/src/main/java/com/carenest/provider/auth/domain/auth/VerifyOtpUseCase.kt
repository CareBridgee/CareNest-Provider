package com.carenest.provider.auth.domain.auth

import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor() {
    suspend operator fun invoke(phoneNumber: String, otp: String): Result<Unit> {
        // Stub implementation
        return Result.success(Unit)
    }
}
