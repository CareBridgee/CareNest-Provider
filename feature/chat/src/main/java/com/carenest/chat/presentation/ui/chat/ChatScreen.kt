package com.carenest.chat.presentation.ui.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.chat.presentation.ui.chat.components.ChatInputBar
import com.carenest.chat.presentation.ui.chat.components.ChatTopBar
import com.carenest.chat.presentation.ui.chat.components.DateSeparatorPill
import com.carenest.chat.presentation.ui.chat.components.MessageBubble
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.presentation.util.dayKey
import com.carenest.chat.presentation.util.dialPhoneNumber
import com.carenest.chat.presentation.util.formatDateSeparator
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    showSnackbar: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(requestId) {
        viewModel.handleIntent(ChatIntent.LoadChat(requestId))
    }

    ObserveEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is ChatEffect.InitiateCall -> dialPhoneNumber(context, effect.phoneNumber)
            ChatEffect.NavigateBack -> onNavigateBack()
            ChatEffect.ScrollToBottom -> coroutineScope.launch {
                if (listState.layoutInfo.totalItemsCount > 0) {
                        listState.animateScrollToItem(0)
                }
            }

            is ChatEffect.ShowError -> showSnackbar(effect.message)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = Theme.colors.backGround,
        topBar = {
            ChatTopBar(
                participantName = state.participant?.name.orEmpty(),
                photoUrl = state.participant?.photoUrl,
                isOnline = state.participant?.isOnline == true,
                onBackClick = { viewModel.handleIntent(ChatIntent.OnBackClicked) },
                onCallClick = { viewModel.handleIntent(ChatIntent.OnCallClicked) },
            )
        },
        bottomBar = {
            ChatInputBar(
                value = state.inputText,
                onValueChange = { value -> viewModel.handleIntent(ChatIntent.OnMessageInputChanged(value)) },
                onSendClick = { viewModel.handleIntent(ChatIntent.OnSendMessageClicked) },
            )
        },
    ) { paddingValues ->
        ChatScreenContent(
            state = state,
            listState = listState,
            onRetryMessage = { msg -> viewModel.handleIntent(ChatIntent.OnRetrySendMessageClicked(msg)) },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ChatScreenContent(
    state: ChatState,
    listState: LazyListState,
    onRetryMessage: (ChatMessage) -> Unit = {},
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Theme.colors.primary,
            )
        } else {
            val groupedMessages = remember(state.messages) {
                state.messages
                    .groupBy { message -> message.dayKey() }
                    .toList()
                    .asReversed()
            }

            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                groupedMessages.forEach { (_, messagesForDay) ->
                    items(
                        items = messagesForDay.asReversed(),
                        key = { message -> message.id },
                    ) { message ->
                        MessageBubble(
                            message = message,
                            onRetryClick = { onRetryMessage(message) },
                        )
                    }

                    val firstMessage = messagesForDay.first()

                    item(key = "date_${firstMessage.id}") {
                        DateSeparatorPill(
                            label = formatDateSeparator(
                                firstMessage.sentAtEpochMillis
                            )
                        )
                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        ChatScreenContent(
            state = ChatState(),
            listState = rememberLazyListState(),

        )
    }
}