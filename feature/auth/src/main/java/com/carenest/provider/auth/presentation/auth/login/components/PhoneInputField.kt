package com.carenest.provider.auth.presentation.auth.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carenest.provider.auth.domain.validation.SupportedPhoneCountry
import com.carenest.provider.auth.presentation.auth.login.Country
import com.carenest.provider.auth.presentation.auth.login.countries
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun PhoneInputField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: Country,
    isDropdownExpanded: Boolean,
    onCountryClick: () -> Unit,
    onCountrySelect: (Country) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.surface,
    fieldHeight: Dp = 60.dp,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val phoneFocusRequester = remember { FocusRequester() }
    val phoneInteractionSource = remember { MutableInteractionSource() }
    val placeholder = selectedCountry.phoneConfig.format(
        "0".repeat(selectedCountry.phoneConfig.nationalDigitLength)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Country Code Block
        Box(
            modifier = Modifier
                .height(fieldHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .clickable(enabled = enabled) { onCountryClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "${selectedCountry.flag} ${selectedCountry.code}",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont
                )
            )

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { onCountryClick() },
                modifier = Modifier
                    .background(
                        color = Theme.colors.surface,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .widthIn(min = 220.dp, max = 280.dp)
                    .heightIn(max = 240.dp)
            ) {
                countries.forEach { country ->
                    val countryName = stringResource(
                        when (country.phoneConfig) {
                            SupportedPhoneCountry.EGYPT -> R.string.phone_country_egypt
                            SupportedPhoneCountry.SAUDI_ARABIA -> R.string.phone_country_saudi_arabia
                            SupportedPhoneCountry.UAE -> R.string.phone_country_uae
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${country.flag} $countryName (${country.code})",
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.primaryFont
                                )
                            )
                        },
                        trailingIcon = if (country == selectedCountry) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Theme.colors.primary,
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .background(
                                if (country == selectedCountry) {
                                    Theme.colors.primaryContainer
                                } else {
                                    Theme.colors.surface
                                }
                            ),
                        onClick = { onCountrySelect(country) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Phone Number Input
        Box(
            modifier = Modifier
                .weight(1f)
                .height(fieldHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .then(
                    if (isError) {
                        Modifier.border(1.dp, Theme.colors.error, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    }
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = phoneInteractionSource,
                    indication = null,
                    onClick = phoneFocusRequester::requestFocus,
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (phone.isEmpty()) {
                BasicText(
                    text = placeholder,
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.hint
                    )
                )
            }
            
            BasicTextField(
                value = phone,
                onValueChange = onPhoneChange,
                enabled = enabled,
                textStyle = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = PhoneNumberVisualTransformation(
                    selectedCountry.phoneConfig.groupSizes
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(phoneFocusRequester),
            )
        }
    }
}

internal class PhoneNumberVisualTransformation(
    groupSizes: List<Int>,
) : VisualTransformation {
    private val groupEndOffsets = groupSizes
        .runningFold(0, Int::plus)
        .drop(1)
        .dropLast(1)

    override fun filter(text: AnnotatedString): TransformedText {
        val activeSeparators = groupEndOffsets.filter { it < text.length }
        val formatted = buildString {
            text.forEachIndexed { index, char ->
                if (index in activeSeparators) append(' ')
                append(char)
            }
        }
        val transformedSeparatorOffsets = activeSeparators.mapIndexed { index, rawOffset ->
            rawOffset + index
        }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val safeOffset = offset.coerceIn(0, text.length)
                    return safeOffset + activeSeparators.count { it <= safeOffset }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val safeOffset = offset.coerceIn(0, formatted.length)
                    return (safeOffset - transformedSeparatorOffsets.count { it < safeOffset })
                        .coerceIn(0, text.length)
                }
            },
        )
    }
}
