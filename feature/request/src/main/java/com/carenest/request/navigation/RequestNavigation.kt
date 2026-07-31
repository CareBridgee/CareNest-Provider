package com.carenest.request.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.request.presentation.ui.details.RequestDetailsScreen
import com.carenest.request.presentation.ui.list.RequestsListScreen
import com.carenest.request.presentation.ui.offerconfirmed.OfferConfirmedScreen
import com.carenest.request.presentation.ui.visit_summary.VisitCompletedScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


val requestSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(RequestRoutes.RequestList::class, RequestRoutes.RequestList.serializer())
        subclass(RequestRoutes.OfferConfirmed::class, RequestRoutes.OfferConfirmed.serializer())
        subclass(RequestRoutes.RequestDetails::class, RequestRoutes.RequestDetails.serializer())
        subclass(RequestRoutes.VisitCompleted::class, RequestRoutes.VisitCompleted.serializer())
    }
}

fun providerRequestStartRoute(): NavKey = RequestRoutes.RequestList

fun EntryProviderScope<NavKey>.providerRequestEntries(
    backStack: SnapshotStateList<NavKey>,
    onNavigateHome: () -> Unit,
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
                // Handle QR code display
            }
        )
    }

    entry<RequestRoutes.RequestDetails> { route ->
        RequestDetailsScreen(
            requestId = route.requestId,
            onBack = { backStack.goBack() }
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