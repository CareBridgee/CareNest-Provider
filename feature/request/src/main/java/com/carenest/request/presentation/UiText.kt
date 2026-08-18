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

fun Throwable.toUiText(): UiText = when (this) {
    is java.net.UnknownHostException, is java.net.ConnectException ->
        UiText.DynamicString("Please check your internet connection and try again.")
    is io.ktor.client.plugins.ResponseException ->
        UiText.DynamicString("We couldn't reach the server right now. Please try again later.")
    else -> UiText.StringResource(R.string.generic_error_message)
}
