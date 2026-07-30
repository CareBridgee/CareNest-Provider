package com.carenest.provider.payouts.data.repository

import com.carenest.provider.designsystem.R
import com.carenest.provider.payouts.domain.model.PayoutItem
import com.carenest.provider.payouts.domain.model.PayoutStatus
import com.carenest.provider.payouts.domain.model.PayoutSummary
import com.carenest.provider.payouts.domain.repository.PayoutsRepository
import javax.inject.Inject

class PayoutsRepositoryImpl @Inject constructor() : PayoutsRepository {
    override suspend fun getPayoutSummary(): Result<PayoutSummary> {
        return Result.success(
            PayoutSummary(
                availableBalance = "$1,248.50",
                pendingAmount = "$320.00",
                thisMonthAmount = "$4,850.00"
            )
        )
    }

    override suspend fun getWithdrawHistory(): Result<List<PayoutItem>> {
        return Result.success(
            listOf(
                PayoutItem(
                    id = "1",
                    methodTitle = "Bank Transfer",
                    dateTime = "Oct 24, 2023 • 09:15 AM",
                    amount = "$450.00",
                    status = PayoutStatus.PENDING,
                    iconRes = R.drawable.ic_bank
                ),
                PayoutItem(
                    id = "2",
                    methodTitle = "Wallet Transfer",
                    dateTime = "Oct 20, 2023 • 04:30 PM",
                    amount = "$1,200.00",
                    status = PayoutStatus.COMPLETED,
                    iconRes = R.drawable.ic_wallet
                ),
                PayoutItem(
                    id = "3",
                    methodTitle = "Bank Transfer",
                    dateTime = "Oct 15, 2023 • 11:00 AM",
                    amount = "$890.00",
                    status = PayoutStatus.COMPLETED,
                    iconRes = R.drawable.ic_bank
                ),
                PayoutItem(
                    id = "4",
                    methodTitle = "Instant Pay",
                    dateTime = "Oct 12, 2023 • 08:45 AM",
                    amount = "$150.00",
                    status = PayoutStatus.FAILED,
                    iconRes = R.drawable.ic_flash
                ),
                PayoutItem(
                    id = "5",
                    methodTitle = "Bank Transfer",
                    dateTime = "Oct 05, 2023 • 02:20 PM",
                    amount = "$2,100.00",
                    status = PayoutStatus.COMPLETED,
                    iconRes = R.drawable.ic_bank
                )
            )
        )
    }

    override suspend fun requestWithdrawal(amount: String): Result<Unit> {
        return Result.success(Unit)
    }
}
