package com.carenest.request.data.remote

import com.carenest.request.BuildConfig
import com.carenest.request.data.remote.dto.ReverseGeocodeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Named

class PatientGeocodingApi @Inject constructor(
    @param:Named("request_location_iq") private val httpClient: HttpClient,
) {
    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): Result<ReverseGeocodeResponse> = runCatching {
        check(BuildConfig.LOCATION_IQ_TOKEN.isNotBlank()) {
            "LocationIQ token is not configured"
        }

        val response = httpClient.get {
            url("https://us1.locationiq.com/v1/reverse")
            parameter("key", BuildConfig.LOCATION_IQ_TOKEN)
            parameter("lat", latitude)
            parameter("lon", longitude)
            parameter("format", "json")
        }
        check(response.status.isSuccess()) {
            "LocationIQ API error: ${response.status}"
        }
        response.body()
    }
}
