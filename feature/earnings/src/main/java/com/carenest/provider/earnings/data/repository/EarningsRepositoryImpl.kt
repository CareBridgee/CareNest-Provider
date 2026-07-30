package com.carenest.provider.earnings.data.repository

import com.carenest.provider.designsystem.R
import com.carenest.provider.earnings.domain.model.EarningStatus
import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.model.ServiceEarningItem
import com.carenest.provider.earnings.domain.repository.EarningsRepository
import javax.inject.Inject

class EarningsRepositoryImpl @Inject constructor() : EarningsRepository {
    override suspend fun getEarningsSummary(): Result<EarningsSummary> {
        return Result.success(
            EarningsSummary(
                totalEarnings = "$4,280.50",
                jobsCount = 34,
                monthName = "This Month"
            )
        )
    }

    override suspend fun getServiceEarnings(): Result<List<ServiceEarningItem>> {
        return Result.success(
            listOf(
                ServiceEarningItem(
                    id = "1",
                    serviceTitle = "Wound Care from Eleanor Rigby",
                    patientName = "Eleanor Rigby",
                    date = "Oct 24, 2023",
                    duration = "2.5 hours",
                    amount = "$125.00",
                    status = EarningStatus.COMPLETED,
                    iconRes = R.drawable.ic_syringe
                ),
                ServiceEarningItem(
                    id = "2",
                    serviceTitle = "Health Assessment from Arthur Dent",
                    patientName = "Arthur Dent",
                    date = "Oct 23, 2023",
                    duration = "1.0 hour",
                    amount = "$85.00",
                    status = EarningStatus.COMPLETED,
                    iconRes = R.drawable.ic_heart_beat
                ),
                ServiceEarningItem(
                    id = "3",
                    serviceTitle = "Meds Management from Sarah Connor",
                    patientName = "Sarah Connor",
                    date = "Oct 22, 2023",
                    duration = "1.5 hours",
                    amount = "$110.00",
                    status = EarningStatus.PROCESSING,
                    iconRes = R.drawable.ic_pill
                ),
                ServiceEarningItem(
                    id = "4",
                    serviceTitle = "Physical Therapy from James Bond",
                    patientName = "James Bond",
                    date = "Oct 20, 2023",
                    duration = "2.0 hours",
                    amount = "$150.00",
                    status = EarningStatus.COMPLETED,
                    iconRes = R.drawable.ic_physical_therapy
                ),
                ServiceEarningItem(
                    id = "5",
                    serviceTitle = "Elderly Companionship from Rose Dawson",
                    patientName = "Rose Dawson",
                    date = "Oct 19, 2023",
                    duration = "4.0 hours",
                    amount = "$200.00",
                    status = EarningStatus.CANCELED,
                    iconRes = R.drawable.ic_elderly
                )
            )
        )
    }
}
