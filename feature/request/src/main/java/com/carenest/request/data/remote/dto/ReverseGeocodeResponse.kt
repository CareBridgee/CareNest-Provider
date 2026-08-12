package com.carenest.request.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodeResponse(
    @SerialName("display_name") val displayName: String? = null,
    val lat: String? = null,
    val lon: String? = null,
    val address: ReverseGeocodeAddressDto? = null,
)

@Serializable
data class ReverseGeocodeAddressDto(
    @SerialName("house_number") val houseNumber: String? = null,
    val road: String? = null,
    val suburb: String? = null,
    @SerialName("city_district") val cityDistrict: String? = null,
    val city: String? = null,
)
