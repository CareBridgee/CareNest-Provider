package com.carenest.provider.account.domain.usecase

import com.carenest.provider.account.domain.model.NurseReviewsPage
import com.carenest.provider.account.domain.repository.ReviewsRepository
import com.carenest.provider.auth.domain.repository.AuthRepository
import java.util.UUID
import javax.inject.Inject

class GetNurseReviewsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewsRepository: ReviewsRepository,
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 10,
        sort: String = "createdAt,desc",
    ): Result<NurseReviewsPage> {
        val userResult = authRepository.getCurrentUser()
        val user = userResult.getOrElse { error ->
            val message = error.message.orEmpty()
            val formattedMsg = if (message.contains("403")) {
                "Access denied (403). Please verify account authorization."
            } else {
                message.ifBlank { "Failed to authenticate current user." }
            }
            return Result.failure(IllegalStateException(formattedMsg, error))
        }

        val nurseId = user.nurse?.id?.trim()?.ifBlank { null } ?: user.id?.trim()?.ifBlank { null }
        if (nurseId.isNullOrBlank() || !isValidUuid(nurseId)) {
            return Result.failure(
                IllegalStateException("Nurse profile is incomplete or nurse ID is invalid.")
            )
        }

        return reviewsRepository.getNurseReviews(
            nurseId = nurseId,
            page = page,
            size = size,
            sort = sort,
        )
    }

    private fun isValidUuid(uuidString: String): Boolean = try {
        UUID.fromString(uuidString)
        true
    } catch (e: Exception) {
        false
    }
}
