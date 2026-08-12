package com.carenest.request.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.request.domain.repository.PatientGeocodingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PatientLocationMapUiState(
    val address: String = "",
    val addressDetail: String = "",
    val isLoading: Boolean = false,
)

@HiltViewModel
class PatientLocationMapViewModel @Inject constructor(
    private val geocodingRepository: PatientGeocodingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PatientLocationMapUiState())
    val state = _state.asStateFlow()

    private var geocodingJob: Job? = null
    private var resolvedCoordinates: Pair<Double, Double>? = null

    fun resolveLocation(
        latitude: Double,
        longitude: Double,
        fallbackAddress: String,
        fallbackDetail: String,
    ) {
        val coordinates = latitude to longitude
        if (resolvedCoordinates == coordinates) return
        resolvedCoordinates = coordinates

        geocodingJob?.cancel()
        geocodingJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    address = fallbackAddress,
                    addressDetail = fallbackDetail,
                    isLoading = true,
                )
            }
            geocodingRepository.reverseGeocode(latitude, longitude)
                .onSuccess { location ->
                    _state.update {
                        it.copy(
                            address = location.address.ifBlank { fallbackAddress },
                            addressDetail = listOf(location.apartment, location.district)
                                .filter(String::isNotBlank)
                                .distinct()
                                .joinToString(", ")
                                .ifBlank { fallbackDetail },
                            isLoading = false,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }
}
