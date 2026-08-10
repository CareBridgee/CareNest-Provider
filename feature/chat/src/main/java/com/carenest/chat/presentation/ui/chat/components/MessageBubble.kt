package com.carenest.chat.presentation.ui.chat.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.chat.R
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import com.carenest.chat.presentation.util.formatMessageTime
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.R as RD

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageBubble(
    message: ChatMessage,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (message.type) {
        ChatMessageType.SYSTEM_TIP -> QuickTipCard(text = message.text, modifier = modifier)
        ChatMessageType.INCOMING -> ChatBubble(
            text = message.text,
            timestamp = formatMessageTime(message.sentAtEpochMillis),
            isOutgoing = false,
            status = null,
            onRetryClick = onRetryClick,
            modifier = modifier,
        )

        ChatMessageType.OUTGOING -> ChatBubble(
            text = message.text,
            timestamp = formatMessageTime(message.sentAtEpochMillis),
            isOutgoing = true,
            status = message.status,
            onRetryClick = onRetryClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun ChatBubble(
    text: String,
    timestamp: String,
    isOutgoing: Boolean,
    status: MessageStatus?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 260.dp),
            horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
        ) {
            Text(
                text = text,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = if (isOutgoing) Theme.colors.onPrimary else Theme.colors.primaryFont,
                modifier = Modifier
                    .background(
                        color = when {
                            status == MessageStatus.FAILED -> Theme.colors.error.copy(alpha = 0.85f)
                            isOutgoing -> Theme.colors.primary
                            else -> Theme.colors.disable
                        },
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
            Spacer(modifier = Modifier.padding(top = 2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timestamp,
                    style = Theme.typography.hint.small,
                    color = Theme.colors.hint,
                )
                if (isOutgoing && status != null) {
                    Spacer(modifier = Modifier.padding(start = 4.dp))
                    when (status) {
                        MessageStatus.SENDING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 1.5.dp,
                                color = Theme.colors.hint,
                            )
                        }
                        MessageStatus.SENT -> {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Sent",
                                tint = Theme.colors.hint,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        MessageStatus.DELIVERED -> {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = "Delivered",
                                tint = Theme.colors.hint,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        MessageStatus.SEEN -> {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = stringResource(R.string.chat_message_seen),
                                tint = Theme.colors.primary,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        MessageStatus.FAILED -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onRetryClick() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = "Failed",
                                    tint = Theme.colors.error,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(modifier = Modifier.padding(start = 2.dp))
                                Text(
                                    text = "Retry",
                                    style = Theme.typography.hint.small,
                                    color = Theme.colors.error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickTipCard(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.primaryContainer, RoundedCornerShape(12.dp))
            .padding(Theme.spacing.medium),
    ) {
        Icon(
            painter = painterResource(RD.drawable.ic_light),
            contentDescription = null,
            tint = Theme.colors.onPrimaryContainer,
            modifier = Modifier.padding(top = Theme.spacing.extraSmall)
        )
        Spacer(modifier = Modifier.padding(start = Theme.spacing.small))
        Column {
            Text(
                text = stringResource(R.string.chat_quick_tip_title),
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Theme.colors.onPrimaryContainer,
            )
            Text(
                text = text,
                style = Theme.typography.body.small,
                color = Theme.colors.onPrimaryContainer,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        MessageBubble(
            message = ChatMessage(
                id = "",
                type = ChatMessageType.OUTGOING,
                text = "Hello Elena! I'm on my way to your\n" + "location. I should be there in about\n" + "10 minutes.",
                senderType = MessageSender.NURSE,
                status = MessageStatus.SEEN,
                sentAtEpochMillis = 0
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuickTipCard() {
    SpTheme {
        QuickTipCard(
            text = "You are communicating with Elena. You\n" + "can share visit updates or coordinates securely through this\n" + "encrypted chat.",
        )
    }
}