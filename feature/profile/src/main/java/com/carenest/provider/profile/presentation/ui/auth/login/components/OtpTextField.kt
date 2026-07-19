package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun OtpTextField(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    otpLength: Int = 6,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = otpValue,
        onValueChange = {
            if (it.length <= otpLength && it.all { char -> char.isDigit() }) {
                onOtpValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    val char = when {
                        index >= otpValue.length -> ""
                        else -> otpValue[index].toString()
                    }
                    val isFocused = otpValue.length == index
                    
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = 1.dp,
                                color = if (isFocused) Theme.colors.primary else Theme.colors.hint,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = char,
                            style = Theme.typography.title.copy(
                                color = Theme.colors.primaryFont,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    )
}
