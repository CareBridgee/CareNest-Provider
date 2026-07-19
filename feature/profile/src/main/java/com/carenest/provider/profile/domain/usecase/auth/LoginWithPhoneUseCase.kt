package com.carenest.provider.profile.domain.usecase.auth

import javax.inject.Inject

class LoginWithPhoneUseCase @Inject constructor() {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> {
        // Stub implementation
        return Result.success(Unit)
    }
}
