package com.carenest.provider.account.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.account.presentation.documents.ProfessionalDocumentsRoute
import com.carenest.provider.account.presentation.profilemenu.ProfileMenuRoute
import com.carenest.provider.account.presentation.publicprofile.PublicProfileRoute
import com.carenest.provider.account.presentation.reviews.RatingsAndReviewsRoute
import com.carenest.provider.account.presentation.settings.SettingsRoute
import com.carenest.provider.account.presentation.wallet.WalletRoute
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
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
        subclass(AccountRoutes.Wallet::class, AccountRoutes.Wallet.serializer())
    }
}

fun providerAccountStartRoute(): NavKey = AccountRoutes.ProfileMenu

fun EntryProviderScope<NavKey>.providerAccountEntries(
    backStack: SnapshotStateList<NavKey>,
    onOpenSupport: () -> Unit = {},
    onLogout: () -> Unit,
) {
    entry<AccountRoutes.ProfileMenu> {
        ProfileMenuRoute(
            onOpenPublicProfile = { backStack.navigate(AccountRoutes.PublicProfile) },
            onOpenDocuments = { backStack.navigate(AccountRoutes.ProfessionalDocuments) },
            onOpenRatingsAndReviews = { backStack.navigate(AccountRoutes.RatingsAndReviews) },
            onOpenSettings = { backStack.navigate(AccountRoutes.Settings) },
            onOpenWallet = { backStack.navigate(AccountRoutes.Wallet) },
            onOpenSupport = onOpenSupport,
            onLogout = onLogout,
        )
    }
    entry<AccountRoutes.PublicProfile> {
        PublicProfileRoute(
            onNavigateBack = { backStack.goBack() },
            onOpenSettings = { backStack.navigate(AccountRoutes.Settings) },
        )
    }
    entry<AccountRoutes.ProfessionalDocuments> {
        ProfessionalDocumentsRoute(onNavigateBack = { backStack.goBack() })
    }
    entry<AccountRoutes.Settings> {
        SettingsRoute(onNavigateBack = { backStack.goBack() })
    }
    entry<AccountRoutes.RatingsAndReviews> {
        RatingsAndReviewsRoute(onNavigateBack = { backStack.goBack() })
    }
    entry<AccountRoutes.Wallet> {
        WalletRoute(onNavigateBack = { backStack.goBack() })
    }
}
