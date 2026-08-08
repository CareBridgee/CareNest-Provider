package com.carenest.provider.profile.domain.repository

import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.NurseRegistration
import com.carenest.provider.profile.domain.model.NurseServiceBatchResult
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.model.ServiceType
import com.carenest.provider.profile.domain.model.UserUpdate

interface ProfileRepository {
    suspend fun updateCurrentUser(request: UserUpdate): Result<String?>
    suspend fun getServiceTypes(): Result<List<ServiceType>>
    suspend fun registerNurse(request: NurseRegistration): Result<NurseProfile>
    suspend fun addServices(nurseId: String, serviceTypeIds: List<String>): Result<NurseServiceBatchResult>
    suspend fun getNurse(nurseId: String): Result<NurseProfile>
    suspend fun updateNurse(nurseId: String, request: NurseUpdate): Result<NurseProfile>
}
