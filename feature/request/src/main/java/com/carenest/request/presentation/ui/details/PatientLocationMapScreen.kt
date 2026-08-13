package com.carenest.request.presentation.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.BuildConfig
import com.carenest.request.R
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

@OptIn(MapboxExperimental::class)
@Composable
fun PatientLocationMapScreen(
    latitude: Double,
    longitude: Double,
    addressLine: String,
    addressDetail: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PatientLocationMapViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(latitude, longitude, addressLine, addressDetail) {
        viewModel.resolveLocation(
            latitude = latitude,
            longitude = longitude,
            fallbackAddress = addressLine,
            fallbackDetail = addressDetail,
        )
    }

    val patientPoint = remember(latitude, longitude) {
        Point.fromLngLat(longitude, latitude)
    }
    val cameraOptions = remember(patientPoint) {
        CameraOptions.Builder()
            .center(patientPoint)
            .zoom(15.0)
            .build()
    }
    remember {
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
        true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        topBar = {
            CareNestTopBar(
                title = stringResource(R.string.request_details_patient_location),
                leading = TopBarLeading.Back(onBack),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            @Suppress("COMPOSE_APPLIES_TO_RIGHT")
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapState = rememberMapState(),
            ) {
                MapEffect(cameraOptions) { mapView ->
                    mapView.mapboxMap.setCamera(cameraOptions)
                }

                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(patientPoint)
                        allowOverlap(true)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium)
                    .shadow(10.dp, Theme.shapes.medium)
                    .clip(Theme.shapes.medium)
                    .background(Theme.colors.surface)
                    .padding(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.primaryContainer.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Theme.colors.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(2.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val displayedAddress = state.address.ifBlank { addressLine }
                    val displayedDetail = state.addressDetail.ifBlank { addressDetail }
                    BasicText(
                        text = if (displayedAddress.isNotBlank()) {
                            displayedAddress
                        } else {
                            stringResource(
                                if (state.isLoading) {
                                    R.string.request_details_finding_address
                                } else {
                                    R.string.request_details_patient_location
                                }
                            )
                        },
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (displayedDetail.isNotBlank() && displayedDetail != displayedAddress) {
                        BasicText(
                            text = displayedDetail,
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.secondaryFont,
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
