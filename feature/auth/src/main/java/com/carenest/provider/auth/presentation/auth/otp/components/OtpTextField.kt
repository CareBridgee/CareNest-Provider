package com.carenest.provider.auth.presentation.auth.otp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun OtpTextField(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    otpLength: Int = 6,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    // Use a single TextFieldValue to manage state and strictly lock the cursor to the end.
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = otpValue, selection = TextRange(otpValue.length)))
    }

    // Keep internal state synced if external state updates unexpectedly
    LaunchedEffect(otpValue) {
        if (textFieldValue.text != otpValue) {
            textFieldValue = TextFieldValue(text = otpValue, selection = TextRange(otpValue.length))
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newTfv ->
            // Filter digits and truncate to the max length
            val digits = newTfv.text.filter { it.isDigit() }.take(otpLength)
            
            // Lock cursor to the end of the text
            textFieldValue = newTfv.copy(text = digits, selection = TextRange(digits.length))
            
            if (digits != otpValue) {
                onOtpValueChange(digits)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        // Hide the actual text input and cursor, as we render the UI in the decorationBox
        textStyle = TextStyle(color = Color.Transparent),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    val hasDigit = char.isNotEmpty()
                    
                    // The "active" box is the first empty box when the field has focus
                    val isCurrentFocus = isFocused && index == otpValue.length

                    // Background color: PrimaryContainer if filled, Surface if actively focused, Disable if empty/unfocused
                    val backgroundColor = when {
                        isCurrentFocus -> Theme.colors.surface
                        hasDigit -> Theme.colors.primaryContainer
                        else -> Theme.colors.disable
                    }

                    // Border logic
                    val (borderColor, borderWidth) = when {
                        isCurrentFocus -> Theme.colors.primary to 2.dp
                        hasDigit -> Theme.colors.primary to 1.dp
                        else -> Color.Transparent to 0.dp
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .border(
                                width = borderWidth,
                                color = borderColor,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = if (hasDigit) char else "-",
                            style = Theme.typography.title.copy(
                                color = if (hasDigit) Theme.colors.primary else Theme.colors.hint,
                                textAlign = TextAlign.Center,
                                fontWeight = if (hasDigit) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    )
}