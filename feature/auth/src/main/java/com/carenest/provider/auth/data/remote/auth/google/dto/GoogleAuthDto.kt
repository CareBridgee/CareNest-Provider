package com.carenest.provider.auth.data.remote.auth.google.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthDto(
    @SerialName("accessToken")
    val accessToken: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("expiresIn")
    val expiresIn: Long? = null,
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("pendingToken")
    val pendingToken: String? = null,
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerialName("refreshToken")
    val refreshToken: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("nurseUser")
    val nurseUser: User? = null,
    @SerialName("user")
    val user: User? = null,
)