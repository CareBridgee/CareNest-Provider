package com.carenest.provider.profile.data.remote

import com.carenest.provider.profile.data.remote.dto.NurseServiceRequestDto
import com.carenest.provider.profile.domain.model.NurseRegistration
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.model.UploadFile
import com.carenest.provider.profile.domain.model.UserUpdate
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import javax.inject.Inject

interface ProfileRemoteDataSource {
    suspend fun updateCurrentUser(request: UserUpdate): HttpResponse
    suspend fun getServiceTypes(): HttpResponse
    suspend fun registerNurse(request: NurseRegistration): HttpResponse
    suspend fun addServices(nurseId: String, requests: List<NurseServiceRequestDto>): HttpResponse
    suspend fun getNurse(nurseId: String): HttpResponse
    suspend fun updateNurse(nurseId: String, request: NurseUpdate): HttpResponse
}

class KtorProfileRemoteDataSource @Inject constructor(
    private val client: HttpClient,
) : ProfileRemoteDataSource {
    override suspend fun updateCurrentUser(request: UserUpdate): HttpResponse =
        client.put("/api/v1/users/me") {
            headers.remove(HttpHeaders.ContentType)
            setBody(MultiPartFormDataContent(formData {
                append("firstName", request.firstName)
                append("lastName", request.lastName)
                request.email?.takeIf(String::isNotBlank)?.let { append("email", it) }
                append("dateOfBirth", request.dateOfBirth)
                append("gender", request.gender)
                request.profileImageUrl?.takeIf(String::isNotBlank)?.let {
                    append("profileImageUrl", it)
                }
                request.profileImage?.let { appendFile("profileImage", it) }
            }))
        }

    override suspend fun getServiceTypes(): HttpResponse = client.get("/api/v1/service-types")

    override suspend fun registerNurse(request: NurseRegistration): HttpResponse =
        client.post("/api/v1/nurses/register") {
            headers.remove(HttpHeaders.ContentType)
            setBody(MultiPartFormDataContent(formData {
                append("nationalId", request.nationalId)
                append("licenseNumber", request.licenseNumber)
                appendFile("nationalIdFront", request.nationalIdFront)
                appendFile("nationalIdBack", request.nationalIdBack)
                appendFile("licenseImage", request.licenseImage)
                appendFile("professionalCertificate", request.professionalCertificate)
                append("specialization", request.specialization)
                append("yearsOfExperience", request.yearsOfExperience.toString())
                request.bio?.takeIf(String::isNotBlank)?.let { append("bio", it) }
                request.profileImage?.let { appendFile("profileImage", it) }
            }))
        }

    override suspend fun addServices(
        nurseId: String,
        requests: List<NurseServiceRequestDto>,
    ): HttpResponse = client.post("/api/v1/nurses/$nurseId/services") { setBody(requests) }

    override suspend fun getNurse(nurseId: String): HttpResponse =
        client.get("/api/v1/nurses/$nurseId")

    override suspend fun updateNurse(nurseId: String, request: NurseUpdate): HttpResponse =
        client.put("/api/v1/nurses/$nurseId") {
            headers.remove(HttpHeaders.ContentType)
            setBody(MultiPartFormDataContent(formData {
                request.nationalId?.let { append("nationalId", it) }
                request.licenseNumber?.let { append("licenseNumber", it) }
                request.nationalIdFront?.let { appendFile("nationalIdFront", it) }
                request.nationalIdBack?.let { appendFile("nationalIdBack", it) }
                request.licenseImage?.let { appendFile("licenseImage", it) }
                request.professionalCertificate?.let { appendFile("professionalCertificate", it) }
                request.specialization?.let { append("specialization", it) }
                request.yearsOfExperience?.let { append("yearsOfExperience", it.toString()) }
                request.bio?.let { append("bio", it) }
                request.profileImage?.let { appendFile("profileImage", it) }
            }))
        }
}

private fun io.ktor.client.request.forms.FormBuilder.appendFile(name: String, file: UploadFile) {
    val safeName = file.fileName.replace("\"", "_")
    append(
        key = name,
        value = file.bytes,
        headers = Headers.build {
            append(HttpHeaders.ContentType, file.mimeType)
            append(HttpHeaders.ContentDisposition, "filename=\"$safeName\"")
        },
    )
}
