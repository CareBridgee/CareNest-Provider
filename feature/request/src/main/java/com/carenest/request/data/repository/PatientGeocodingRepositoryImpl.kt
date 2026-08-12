package com.carenest.request.data.repository

import com.carenest.request.data.remote.ProviderGeocodingApi
import com.carenest.request.domain.model.PatientLocationDetails
import com.carenest.request.domain.repository.PatientGeocodingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PatientGeocodingRepositoryImpl @Inject constructor(
    private val api: ProviderGeocodingApi,
) : PatientGeocodingRepository {

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): Result<PatientLocationDetails> = withContext(Dispatchers.IO) {
        api.reverseGeocode(latitude, longitude).map { response ->
            val apartment = listOfNotNull(
                response.address?.houseNumber,
                response.address?.road,
            ).filter(String::isNotBlank).joinToString(" ")
            val district = response.address?.suburb
                ?: response.address?.cityDistrict
                ?: response.address?.city
                ?: ""

            PatientLocationDetails(
                address = response.displayName.orEmpty(),
                apartment = apartment,
                district = district,
                latitude = response.lat?.toDoubleOrNull() ?: latitude,
                longitude = response.lon?.toDoubleOrNull() ?: longitude,
            )
        }
    }
}
