package com.carenest.chat.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.chat.presentation.ui.chat.ChatScreen
import com.carenest.provider.core.navigation.goBack
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


val chatSerializers = SerializersModule {
    polymorphic(NavKey::class){
        subclass(ChatRoutes.Chat::class, ChatRoutes.Chat.serializer())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun EntryProviderScope<NavKey>.providerChatEntries(
    backStack: SnapshotStateList<NavKey>,
){
    entry<ChatRoutes.Chat> { route ->
        ChatScreen(
            requestId = route.requestId,
            onNavigateBack = { backStack.goBack() },
            showSnackbar = {}
        )
    }
}