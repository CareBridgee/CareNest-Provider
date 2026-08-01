package com.carenest.chat.presentation.ui.chat.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.chat.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.surface)
            .padding(horizontal = Theme.spacing.space12, vertical = Theme.spacing.space10),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Theme.colors.disable, RoundedCornerShape(24.dp))
                .padding(Theme.spacing.medium, Theme.spacing.space12),
        ) {

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Theme.colors.primaryFont,
                    fontSize = Theme.typography.body.medium.fontSize,
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.chat_input_placeholder),
                                style = Theme.typography.body.medium.copy(
                                    fontWeight = FontWeight.Normal
                                ),
                                color = Theme.colors.hint,
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.padding(start = Theme.spacing.small))

        IconButton(
            onClick = onSendClick,
            enabled = value.isNotBlank(),
            modifier = Modifier
                .size(40.dp)
                .background(Theme.colors.primary, CircleShape),
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = stringResource(R.string.chat_send_content_description),
                tint = Theme.colors.onPrimary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        ChatInputBar(
            value = "",
            onValueChange = {},
            onSendClick = {},
        )
    }
}
