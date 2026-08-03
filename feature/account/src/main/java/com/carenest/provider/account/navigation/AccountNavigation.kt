package com.carenest.provider.account.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.account.presentation.documents.ProfessionalDocumentsRoute
import com.carenest.provider.account.presentation.profilemenu.ProfileMenuRoute
import com.carenest.provider.account.presentation.publicprofile.PublicProfileRoute
import com.carenest.provider.account.presentation.reviews.RatingsAndReviewsRoute
import com.carenest.provider.account.presentation.settings.SettingsRoute
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val accountNavigationSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AccountRoutes.ProfileMenu::class, AccountRoutes.ProfileMenu.serializer())
        subclass(AccountRoutes.PublicProfile::class, AccountRoutes.PublicProfile.serializer())
        subclass(
            AccountRoutes.ProfessionalDocuments::class,
            AccountRoutes.ProfessionalDocuments.serializer(),
        )
        subclass(AccountRoutes.Settings::class, AccountRoutes.Settings.serializer())
        subclass(
            AccountRoutes.RatingsAndReviews::class,
            AccountRoutes.RatingsAndReviews.serializer(),
        )
    }
}

fun providerAccountStartRoute(): NavKey = AccountRoutes.ProfileMenu

fun EntryProviderScope<NavKey>.providerAccountEntries(
    onOpenPublicProfile: () -> Unit,
    onOpenDocuments: () -> Unit,
    onOpenRatingsAndReviews: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEarnings: () -> Unit,
    onOpenPayouts: () -> Unit,
    onOpenSupport: () -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    entry<AccountRoutes.ProfileMenu> {
        ProfileMenuRoute(
            onOpenPublicProfile = onOpenPublicProfile,
            onOpenDocuments = onOpenDocuments,
            onOpenRatingsAndReviews = onOpenRatingsAndReviews,
            onOpenSettings = onOpenSettings,
            onOpenEarnings = onOpenEarnings,
            onOpenPayouts = onOpenPayouts,
            onOpenSupport = onOpenSupport,
            onLogout = onLogout,
        )
    }
    entry<AccountRoutes.PublicProfile> {
        PublicProfileRoute(
            onNavigateBack = onNavigateBack,
            onOpenSettings = onOpenSettings,
        )
    }
    entry<AccountRoutes.ProfessionalDocuments> {
        ProfessionalDocumentsRoute(onNavigateBack = onNavigateBack)
    }
    entry<AccountRoutes.Settings> {
        SettingsRoute(onNavigateBack = onNavigateBack)
    }
    entry<AccountRoutes.RatingsAndReviews> {
        RatingsAndReviewsRoute(onNavigateBack = onNavigateBack)
    }
}

