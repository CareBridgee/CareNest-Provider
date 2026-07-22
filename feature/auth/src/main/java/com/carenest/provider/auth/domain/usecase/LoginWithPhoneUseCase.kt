package com.carenest.provider.auth.domain.usecase

import com.carenest.provider.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithPhoneUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> {
        return repository.login(phoneNumber)
    }
}
