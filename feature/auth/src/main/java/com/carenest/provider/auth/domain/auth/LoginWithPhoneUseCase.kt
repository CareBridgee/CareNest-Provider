package com.carenest.provider.auth.domain.auth

import javax.inject.Inject

class LoginWithPhoneUseCase @Inject constructor() {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> {
        // Stub implementation
        return Result.success(Unit)
    }
}
