package com.carenest.provider.auth.domain.usecase

import com.carenest.provider.auth.domain.repository.AuthRepository
import javax.inject.Inject

class DevLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): Result<String> {
        return repository.devLogin(phoneNumber)
    }
}
