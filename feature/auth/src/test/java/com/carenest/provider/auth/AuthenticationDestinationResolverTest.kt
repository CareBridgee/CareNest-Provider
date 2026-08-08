package com.carenest.provider.auth

import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.repository.AuthenticatedNurse
import com.carenest.provider.auth.domain.repository.AuthenticatedUser
import com.carenest.provider.auth.domain.repository.NurseVerificationStatus
import com.carenest.provider.auth.domain.usecase.AuthenticationDestinationResolver
import com.carenest.provider.auth.domain.usecase.ResolveAuthenticationDestinationUseCase
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.core.util.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthenticationDestinationResolverTest {
    private val resolver = AuthenticationDestinationResolver()

    @Test
    fun `incomplete profile overrides under review placeholder`() {
        val destination = resolver.resolve(
            profileCompleted = false,
            nurse = nurse(NurseVerificationStatus.UNDER_REVIEW),
        )

        assertEquals(AuthenticationDestination.CompleteProfile, destination)
    }

    @Test
    fun `incomplete profile overrides approved status`() {
        val destination = resolver.resolve(
            profileCompleted = false,
            nurse = nurse(NurseVerificationStatus.APPROVED),
        )

        assertEquals(AuthenticationDestination.CompleteProfile, destination)
    }

    @Test
    fun `completed profile under review routes to review`() {
        val destination = resolver.resolve(
            profileCompleted = true,
            nurse = nurse(NurseVerificationStatus.UNDER_REVIEW),
        )

        assertEquals(AuthenticationDestination.UnderReview(NURSE_ID), destination)
    }

    @Test
    fun `completed approved profile routes home`() {
        val destination = resolver.resolve(
            profileCompleted = true,
            nurse = nurse(NurseVerificationStatus.APPROVED),
        )

        assertEquals(AuthenticationDestination.Approved(NURSE_ID), destination)
    }

    @Test
    fun `completed rejected profile routes to action required`() {
        val destination = resolver.resolve(
            profileCompleted = true,
            nurse = nurse(NurseVerificationStatus.REJECTED),
        )

        assertEquals(AuthenticationDestination.Rejected(NURSE_ID), destination)
    }

    @Test
    fun `completed user without nurse routes to complete profile`() {
        val destination = resolver.resolve(profileCompleted = true, nurse = null)

        assertEquals(AuthenticationDestination.CompleteProfile, destination)
    }

    @Test
    fun `users me failure does not produce a guessed destination`() = runBlocking {
        val expected = IllegalStateException("current user unavailable")
        val useCase = ResolveAuthenticationDestinationUseCase(
            repository = FakeAuthRepository(listOf(Result.failure(expected))),
            resolver = resolver,
        )

        val result = useCase(nurse(NurseVerificationStatus.UNDER_REVIEW))

        assertTrue(result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `users me failure can be retried without verifying otp again`() = runBlocking {
        val repository = FakeAuthRepository(
            listOf(
                Result.failure(IllegalStateException("temporary failure")),
                Result.success(AuthenticatedUser(profileCompleted = false)),
            )
        )
        val useCase = ResolveAuthenticationDestinationUseCase(repository, resolver)
        val nurse = nurse(NurseVerificationStatus.UNDER_REVIEW)

        val firstAttempt = useCase(nurse)
        val retry = useCase(nurse)

        assertTrue(firstAttempt.isFailure)
        assertEquals(AuthenticationDestination.CompleteProfile, retry.getOrNull())
        assertEquals(2, repository.currentUserCalls)
        assertEquals(0, repository.verifyOtpCalls)
    }

    private fun nurse(status: NurseVerificationStatus) =
        AuthenticatedNurse(id = NURSE_ID, verificationStatus = status)

    private class FakeAuthRepository(
        private val currentUserResults: List<Result<AuthenticatedUser>>,
    ) : AuthRepository {
        var currentUserCalls: Int = 0
            private set
        var verifyOtpCalls: Int = 0
            private set

        override suspend fun login(phoneNumber: String): Result<Unit> = Result.success(Unit)
        override suspend fun devLogin(phoneNumber: String): Resource<String> {
            TODO("Not yet implemented")
        }

        override suspend fun verifyOtp(
            phoneNumber: String,
            otp: String,
        ): Result<AuthenticatedNurse?> {
            verifyOtpCalls += 1
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun getCurrentUser(): Result<AuthenticatedUser> =
            currentUserResults[currentUserCalls++]
    }

    private companion object {
        const val NURSE_ID = "9dcfd8af-c7e7-407d-b156-0f50b2298285"
    }
}
