package com.carenest.provider.auth.data.remote.auth.google.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("createdAt")
    val createdAt: String?,
    @SerialName("dateOfBirth")
    val dateOfBirth: String?,
    @SerialName("defaultProfileId")
    val defaultProfileId: String?,
    @SerialName("email")
    val email: String?,
    @SerialName("firstName")
    val firstName: String?,
    @SerialName("gender")
    val gender: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("isDeleted")
    val isDeleted: Boolean?,
    @SerialName("lastLoginAt")
    val lastLoginAt: String?,
    @SerialName("lastName")
    val lastName: String?,
    @SerialName("phoneNumber")
    val phoneNumber: String?,
    @SerialName("profileCompleted")
    val profileCompleted: Boolean?,
    @SerialName("profileImageUrl")
    val profileImageUrl: String?,
    @SerialName("updatedAt")
    val updatedAt: String?
)