package com.carenest.request.domain.repository

import com.carenest.request.domain.model.PatientLocationDetails

interface PatientGeocodingRepository {
    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): Result<PatientLocationDetails>
}
