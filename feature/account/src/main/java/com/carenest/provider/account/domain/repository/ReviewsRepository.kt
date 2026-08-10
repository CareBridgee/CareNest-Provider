package com.carenest.provider.account.domain.repository

import com.carenest.provider.account.domain.model.NurseReviewsPage

interface ReviewsRepository {
    suspend fun getNurseReviews(
        nurseId: String,
        page: Int = 0,
        size: Int = 10,
        sort: String = "createdAt,desc",
    ): Result<NurseReviewsPage>
}
