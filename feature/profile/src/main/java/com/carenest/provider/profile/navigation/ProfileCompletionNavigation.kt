package com.carenest.provider.profile.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.provider.core.navigation.replaceWith
import com.carenest.provider.profile.presentation.ui.registration.RegistrationScreen
import com.carenest.provider.profile.presentation.ui.reupload_document_screen.ReUploadDocumentScreen
import com.carenest.provider.profile.presentation.ui.under_review_screen.UnderReviewScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val profileCompletionNavigationSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(RegistrationRoute::class, RegistrationRoute.serializer())
        subclass(UnderReviewRoute::class, UnderReviewRoute.serializer())
        subclass(ReUploadDocumentRoute::class, ReUploadDocumentRoute.serializer())
    }
}

fun providerProfileCompletionStartRoute(): NavKey = RegistrationRoute

fun EntryProviderScope<NavKey>.providerProfileCompletionEntries(
    backStack: SnapshotStateList<NavKey>,
    onBackToAuthentication: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenContactSupport: () -> Unit,
    onOpenCommunityGuidelines: () -> Unit,
    onExitRequested: () -> Unit,
) {
    fun navigateBack() {
        if (!backStack.goBack()) {
            onExitRequested()
        }
    }

    entry<RegistrationRoute> {
        RegistrationScreen(
            onNavigateToApplicationUnderReview = {
                backStack.replaceWith(UnderReviewRoute)
            },
        )
    }
    entry<UnderReviewRoute> {
        UnderReviewScreen(
            onBackClick = ::navigateBack,
            onGoToHomeClick = onOpenDashboard,
            onContactSupportClick = onOpenContactSupport,
            onBackToLoginClick = onBackToAuthentication,
            onDashboardClick = onOpenDashboard,
            onCommunityGuidelinesClick = onOpenCommunityGuidelines,
            onUploadAgainClick = { backStack.navigate(ReUploadDocumentRoute) },
        )
    }
    entry<ReUploadDocumentRoute> {
        ReUploadDocumentScreen(
            onBackClick = ::navigateBack,
            onUploadSuccess = { backStack.replaceWith(UnderReviewRoute) },
        )
    }
}
