package com.carenest.provider.profile.domain.model

data class UploadFile(
    val fileName: String,
    val mimeType: String,
    val bytes: ByteArray,
)

data class UserUpdate(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val dateOfBirth: String,
    val gender: String,
    val profileImageUrl: String? = null,
    val profileImage: UploadFile? = null,
)

data class NurseRegistration(
    val nationalId: String,
    val licenseNumber: String,
    val nationalIdFront: UploadFile,
    val nationalIdBack: UploadFile,
    val licenseImage: UploadFile,
    val professionalCertificate: UploadFile,
    val specialization: String,
    val yearsOfExperience: Int,
    val bio: String? = null,
    val profileImage: UploadFile? = null,
)

data class NurseUpdate(
    val nationalId: String? = null,
    val licenseNumber: String? = null,
    val nationalIdFront: UploadFile? = null,
    val nationalIdBack: UploadFile? = null,
    val licenseImage: UploadFile? = null,
    val professionalCertificate: UploadFile? = null,
    val specialization: String? = null,
    val yearsOfExperience: Int? = null,
    val bio: String? = null,
    val profileImage: UploadFile? = null,
)

data class ServiceType(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
)

enum class VerificationStatus { UNDER_REVIEW, APPROVED, REJECTED }

data class FailedStep(
    val step: String,
    val reason: String,
)

data class NurseProfile(
    val id: String,
    val profileImageUrl: String?,
    val verificationStatus: VerificationStatus,
    val rejectionReason: String?,
    val failedSteps: List<FailedStep>,
)

data class ServiceFailure(
    val serviceTypeId: String,
    val reason: String,
)

data class NurseServiceBatchResult(
    val addedServiceTypeIds: List<String>,
    val failed: List<ServiceFailure>,
)

data class RegistrationSubmission(
    val user: UserUpdate,
    val nurse: NurseRegistration,
    val serviceTypeIds: List<String>,
)
