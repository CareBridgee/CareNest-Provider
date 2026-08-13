package com.carenest.provider.auth

import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.model.AuthenticatedUser
import com.carenest.provider.auth.domain.model.GoogleLoginResult
import com.carenest.provider.auth.domain.model.NurseVerificationStatus
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.usecase.GoogleLoginUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleLoginUseCaseTest {

    @Test
    fun googleLoginReturnsAuthenticatedOnSuccess() = runTest {
        val expectedNurse = AuthenticatedNurse(
            id = "nurse-123",
            verificationStatus = NurseVerificationStatus.APPROVED,
        )
        val repository = FakeGoogleAuthRepository(
            googleLoginResult = Result.success(GoogleLoginResult.Authenticated(expectedNurse)),
        )
        val useCase = GoogleLoginUseCase(repository)

        val result = useCase("valid-id-token")

        assertTrue(result.isSuccess)
        val googleResult = result.getOrNull()
        assertTrue(googleResult is GoogleLoginResult.Authenticated)
        assertEquals(expectedNurse, (googleResult as GoogleLoginResult.Authenticated).nurse)
    }

    @Test
    fun googleLoginReturnsPhoneRequiredWhenPhoneNeeded() = runTest {
        val repository = FakeGoogleAuthRepository(
            googleLoginResult = Result.success(
                GoogleLoginResult.PhoneRequired(
                    pendingToken = "pending-123",
                    email = "nurse@example.com",
                    firstName = "Jane",
                    lastName = "Doe",
                ),
            ),
        )
        val useCase = GoogleLoginUseCase(repository)

        val result = useCase("valid-id-token")

        assertTrue(result.isSuccess)
        val googleResult = result.getOrNull()
        assertTrue(googleResult is GoogleLoginResult.PhoneRequired)
        val phoneReq = googleResult as GoogleLoginResult.PhoneRequired
        assertEquals("pending-123", phoneReq.pendingToken)
        assertEquals("nurse@example.com", phoneReq.email)
    }
}

private class FakeGoogleAuthRepository(
    private val googleLoginResult: Result<GoogleLoginResult>,
) : AuthRepository {
    override suspend fun login(phoneNumber: String): Result<Unit> = Result.success(Unit)
    override suspend fun devLogin(phoneNumber: String): Result<String> = Result.success("123456")
    override suspend fun googleLogin(
        idToken: String,
        firstName: String?,
        lastName: String?,
        email: String?,
        profileImageUrl: String?,
    ): Result<GoogleLoginResult> = googleLoginResult
    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String?,
    ): Result<AuthenticatedNurse?> = Result.success(null)
    override suspend fun getCurrentUser(): Result<AuthenticatedUser> =
        Result.success(AuthenticatedUser(profileCompleted = false))
}
