package com.carenest.request.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.request.presentation.ui.details.OfferDetailsScreen
import com.carenest.request.presentation.ui.details.PatientLocationMapScreen
import com.carenest.request.presentation.ui.list.RequestsListScreen
import com.carenest.request.presentation.ui.offerconfirmed.OfferConfirmedScreen
import com.carenest.request.presentation.ui.scan.ScanQrScreen
import com.carenest.request.presentation.ui.visit_summary.VisitCompletedScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


val requestSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(RequestRoutes.RequestList::class, RequestRoutes.RequestList.serializer())
        subclass(RequestRoutes.OfferConfirmed::class, RequestRoutes.OfferConfirmed.serializer())
        subclass(RequestRoutes.RequestDetails::class, RequestRoutes.RequestDetails.serializer())
        subclass(RequestRoutes.PatientLocation::class, RequestRoutes.PatientLocation.serializer())
        subclass(RequestRoutes.VisitCompleted::class, RequestRoutes.VisitCompleted.serializer())
        subclass(RequestRoutes.ScanQr::class, RequestRoutes.ScanQr.serializer())
    }
}

fun providerRequestStartRoute(): NavKey = RequestRoutes.RequestList

fun EntryProviderScope<NavKey>.providerRequestEntries(
    backStack: SnapshotStateList<NavKey>,
    onNavigateHome: () -> Unit,
    onOpenChat: (String) -> Unit,
){
    entry<RequestRoutes.RequestList> {
        RequestsListScreen(
            onBack = { backStack.goBack() },
            onOfferConfirmed = { requestId ->
                backStack.navigate(RequestRoutes.OfferConfirmed(requestId))
            },
            onViewDetails = { requestId ->
                backStack.navigate(RequestRoutes.RequestDetails(requestId))
            }
        )
    }

    entry<RequestRoutes.OfferConfirmed> { route ->
        OfferConfirmedScreen(
            requestId = route.requestId,
            onViewDetails = { requestId ->
                backStack.navigate(RequestRoutes.RequestDetails(requestId))
            },
            onCancelled = {
                backStack.goBack()
            },
            onShowQrCode = {
                backStack.navigate(RequestRoutes.ScanQr(route.requestId))
            },
            onOpenChat = onOpenChat,
            onVisitCompleted = { requestId ->
                backStack.navigate(RequestRoutes.VisitCompleted(requestId))
            },
        )
    }

    entry<RequestRoutes.PatientLocation> { route ->
        PatientLocationMapScreen(
            latitude = route.latitude,
            longitude = route.longitude,
            addressLine = route.addressLine,
            addressDetail = route.addressDetail,
            onBack = { backStack.goBack() },
        )
    }

    entry<RequestRoutes.ScanQr> { route ->
        ScanQrScreen(
            requestId = route.requestId,
            onBack = { backStack.goBack() },
            onSuccess = { backStack.navigate(RequestRoutes.VisitCompleted(route.requestId)) }
        )
    }

    entry<RequestRoutes.RequestDetails> { route ->
        OfferDetailsScreen(
            requestId = route.requestId,
            onBack = { backStack.goBack() },
            onOpenChat = onOpenChat,
            onVisitCompleted = { requestId ->
                backStack.navigate(RequestRoutes.VisitCompleted(requestId))
            },
            onOpenPatientLocation = { latitude, longitude, addressLine, addressDetail ->
                backStack.navigate(
                    RequestRoutes.PatientLocation(
                        latitude = latitude,
                        longitude = longitude,
                        addressLine = addressLine,
                        addressDetail = addressDetail,
                    )
                )
            },
        )
    }

    entry<RequestRoutes.VisitCompleted> { route->
        VisitCompletedScreen(
            requestId = route.requestId,
            onNavigateHome = onNavigateHome,
            onShowSnackbar = {}
        )
    }
}
