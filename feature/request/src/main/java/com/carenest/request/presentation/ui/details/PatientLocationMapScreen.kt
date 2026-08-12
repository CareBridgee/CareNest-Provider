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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.BuildConfig
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
) {
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

    Box(modifier = modifier.fillMaxSize()) {
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
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(42.dp),
                )
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Theme.spacing.medium)
                .padding(top = 24.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Theme.colors.surface)
                .size(44.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Theme.colors.primary,
            )
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
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(2.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (addressLine.isNotBlank()) {
                    BasicText(
                        text = addressLine,
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (addressDetail.isNotBlank()) {
                    BasicText(
                        text = addressDetail,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.secondaryFont,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                BasicText(
                    text = "%.5f, %.5f".format(latitude, longitude),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont.copy(alpha = 0.7f),
                    ),
                )
            }
        }
    }
}
