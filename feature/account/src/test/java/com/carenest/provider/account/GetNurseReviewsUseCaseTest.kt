package com.carenest.provider.account

import com.carenest.provider.account.domain.model.NurseReview
import com.carenest.provider.account.domain.model.NurseReviewsPage
import com.carenest.provider.account.domain.repository.ReviewsRepository
import com.carenest.provider.account.domain.usecase.GetNurseReviewsUseCase
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.repository.AuthenticatedNurse
import com.carenest.provider.auth.domain.repository.AuthenticatedUser
import com.carenest.provider.auth.domain.repository.NurseVerificationStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNurseReviewsUseCaseTest {

    private class FakeAuthRepository(
        private val userResult: Result<AuthenticatedUser>,
    ) : AuthRepository {
        override suspend fun login(phoneNumber: String): Result<Unit> = Result.success(Unit)
        override suspend fun devLogin(phoneNumber: String): Result<String> = Result.success("123456")
        override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthenticatedNurse?> =
            Result.success(userResult.getOrNull()?.nurse)
        override suspend fun getCurrentUser(): Result<AuthenticatedUser> = userResult
    }

    private class FakeReviewsRepository(
        private val handler: (nurseId: String) -> Result<NurseReviewsPage>,
    ) : ReviewsRepository {
        override suspend fun getNurseReviews(
            nurseId: String,
            page: Int,
            size: Int,
            sort: String,
        ): Result<NurseReviewsPage> = handler(nurseId)
    }

    @Test
    fun `invoke returns reviews page when nurse UUID exists and repository succeeds`() = runTest {
        val nurseId = "123e4567-e89b-12d3-a456-426614174000"
        val expectedPage = NurseReviewsPage(
            totalElements = 1,
            totalPages = 1,
            isFirst = true,
            isLast = true,
            pageNumber = 0,
            pageSize = 10,
            reviews = listOf(
                NurseReview(
                    id = "rev-1",
                    bookingId = "b-1",
                    profileId = "p-1",
                    nurseId = nurseId,
                    rating = 5,
                    reviewText = "Great care!",
                    isAnonymous = false,
                    createdAt = "2026-08-10T11:21:38.430Z",
                    updatedAt = null,
                ),
            ),
        )

        val authRepo = FakeAuthRepository(
            Result.success(
                AuthenticatedUser(
                    profileCompleted = true,
                    nurse = AuthenticatedNurse(id = nurseId, verificationStatus = NurseVerificationStatus.APPROVED),
                ),
            ),
        )
        val reviewsRepo = FakeReviewsRepository { Result.success(expectedPage) }
        val useCase = GetNurseReviewsUseCase(authRepo, reviewsRepo)

        val result = useCase(page = 0, size = 10, sort = "createdAt,desc")

        assertTrue(result.isSuccess)
        assertEquals(expectedPage, result.getOrNull())
    }

    @Test
    fun `invoke fails when nurse ID is not a valid UUID`() = runTest {
        val authRepo = FakeAuthRepository(
            Result.success(
                AuthenticatedUser(
                    profileCompleted = true,
                    nurse = AuthenticatedNurse(id = "invalid-uuid-string", verificationStatus = NurseVerificationStatus.APPROVED),
                ),
            ),
        )
        val reviewsRepo = FakeReviewsRepository { Result.success(NurseReviewsPage(0, 0, true, true, 0, 10, emptyList())) }
        val useCase = GetNurseReviewsUseCase(authRepo, reviewsRepo)

        val result = useCase(page = 0, size = 10, sort = "createdAt,desc")

        assertTrue(result.isFailure)
        assertEquals("Nurse profile is incomplete or nurse ID is invalid.", result.exceptionOrNull()?.message)
    }
}
