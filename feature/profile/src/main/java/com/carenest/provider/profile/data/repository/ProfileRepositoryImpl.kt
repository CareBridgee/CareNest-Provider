package com.carenest.provider.profile.data.repository

import com.carenest.provider.profile.data.remote.ProfileRemoteDataSource
import com.carenest.provider.profile.data.remote.dto.ErrorResponseDto
import com.carenest.provider.profile.data.remote.dto.NurseResponseDto
import com.carenest.provider.profile.data.remote.dto.NurseServiceBatchResultDto
import com.carenest.provider.profile.data.remote.dto.NurseServiceRequestDto
import com.carenest.provider.profile.data.remote.dto.ServiceTypeResponseDto
import com.carenest.provider.profile.data.remote.dto.UserResponseDto
import com.carenest.provider.profile.domain.model.FailedStep
import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.NurseService
import com.carenest.provider.profile.domain.model.NurseRegistration
import com.carenest.provider.profile.domain.model.NurseServiceBatchResult
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.model.ServiceFailure
import com.carenest.provider.profile.domain.model.ServiceType
import com.carenest.provider.profile.domain.model.UserUpdate
import com.carenest.provider.profile.domain.model.VerificationStatus
import com.carenest.provider.profile.domain.repository.ProfileRepository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dataSource: ProfileRemoteDataSource,
    private val json: Json,
) : ProfileRepository {
    override suspend fun updateCurrentUser(request: UserUpdate): Result<String?> = execute {
        dataSource.updateCurrentUser(request)
            .successBody<UserResponseDto>()
            .profileImageUrl
    }

    override suspend fun getServiceTypes(): Result<List<ServiceType>> = execute {
        dataSource.getServiceTypes().successBody<List<ServiceTypeResponseDto>>().map {
            ServiceType(it.id, it.name, it.description, it.imageUrl)
        }
    }

    override suspend fun registerNurse(request: NurseRegistration): Result<NurseProfile> = execute {
        dataSource.registerNurse(request).successBody<NurseResponseDto>().toDomain()
    }

    override suspend fun addServices(
        nurseId: String,
        serviceTypeIds: List<String>,
    ): Result<NurseServiceBatchResult> = execute {
        dataSource.addServices(nurseId, serviceTypeIds.map(::NurseServiceRequestDto))
            .successBody<NurseServiceBatchResultDto>()
            .let { dto ->
                NurseServiceBatchResult(
                    addedServiceTypeIds = dto.added.map { it.serviceTypeId },
                    failed = dto.failed.map {
                        ServiceFailure(it.serviceTypeId, it.reason ?: "Unknown backend error")
                    },
                )
            }
    }

    override suspend fun getNurse(nurseId: String): Result<NurseProfile> = execute {
        dataSource.getNurse(nurseId).successBody<NurseResponseDto>().toDomain()
    }

    override suspend fun updateNurse(nurseId: String, request: NurseUpdate): Result<NurseProfile> = execute {
        dataSource.updateNurse(nurseId, request).successBody<NurseResponseDto>().toDomain()
    }

    private suspend inline fun <T> execute(crossinline block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (error: Exception) {
            Result.failure(error)
        }

    private suspend inline fun <reified T> HttpResponse.successBody(): T {
        if (!status.isSuccess()) throw backendException()
        return body()
    }

    private suspend fun HttpResponse.backendException(): Exception {
        val raw = runCatching { bodyAsText() }.getOrDefault("")
        val parsed = runCatching { json.decodeFromString<ErrorResponseDto>(raw) }.getOrNull()
        val message = parsed?.message ?: parsed?.error ?: parsed?.details
        return IllegalStateException(
            message?.takeIf(String::isNotBlank)
                ?: raw.takeIf(String::isNotBlank)
                ?: "HTTP ${status.value} (${status.description})"
        )
    }
}

private fun NurseResponseDto.toDomain(): NurseProfile = NurseProfile(
    id = id,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    profileImageUrl = profileImageUrl,
    nationalId = nationalId,
    nationalIdFrontUrl = nationalIdFrontUrl,
    nationalIdBackUrl = nationalIdBackUrl,
    licenseNumber = licenseNumber,
    licenseImageUrl = licenseImageUrl,
    professionalCertificateUrl = professionalCertificateUrl,
    specialization = specialization,
    yearsOfExperience = yearsOfExperience,
    bio = bio,
    ratingAvg = ratingAvg,
    totalReviews = totalReviews,
    verificationStatus = verificationStatus.toVerificationStatus(),
    rejectionReason = rejectionDetails?.overallReason ?: rejectionReason,
    failedSteps = rejectionDetails?.failedSteps.orEmpty().mapNotNull { item ->
        val step = item.step ?: return@mapNotNull null
        FailedStep(step, item.reason.orEmpty())
    },
    services = services.mapNotNull { service ->
        val name = service.serviceName?.takeIf(String::isNotBlank) ?: return@mapNotNull null
        NurseService(
            id = service.id,
            serviceTypeId = service.serviceTypeId,
            serviceName = name,
            serviceDescription = service.serviceDescription,
            basePrice = service.basePrice,
            isActive = service.isActive,
        )
    },
)

private fun String.toVerificationStatus(): VerificationStatus =
    runCatching { VerificationStatus.valueOf(this) }
        .getOrDefault(VerificationStatus.UNDER_REVIEW)
