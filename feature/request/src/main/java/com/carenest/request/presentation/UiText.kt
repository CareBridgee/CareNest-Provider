package com.carenest.request.presentation

import android.content.Context
import androidx.annotation.StringRes
import com.carenest.request.R

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(@param:StringRes val resId: Int) : UiText
}

fun UiText.asString(context: Context): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> context.getString(resId)
}

fun Throwable.toUiText(): UiText = message
    ?.takeIf(String::isNotBlank)
    ?.let(UiText::DynamicString)
    ?: UiText.StringResource(R.string.generic_error_message)
