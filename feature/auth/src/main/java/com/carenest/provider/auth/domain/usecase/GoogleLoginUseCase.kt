package com.carenest.provider.auth.domain.usecase

import com.carenest.provider.auth.domain.model.GoogleLoginResult
import com.carenest.provider.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        idToken: String,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        profileImageUrl: String? = null,
    ): Result<GoogleLoginResult> {
        return repository.googleLogin(idToken, firstName, lastName, email, profileImageUrl)
    }
}
