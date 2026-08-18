package com.carenest.provider.account.presentation.publicprofile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns

internal fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use { current ->
            if (current != null && current.moveToFirst()) {
                val index = current.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = current.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val lastSlash = result?.lastIndexOf('/')
        if (lastSlash != null && lastSlash != -1) {
            result = result.substring(lastSlash + 1)
        }
    }
    return result ?: "upload"
}

internal fun persistReadPermission(context: Context, uri: Uri) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}

internal fun resolveMessage(context: Context, message: String): String {
    val resId = context.resources.getIdentifier(message, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else message
}

internal fun shareProfile(context: Context, nurseId: String, name: String, specialization: String) {
    val profileUrl = "https://carenest.com/nurse/$nurseId"
    val shareMessage = "Check out $name ($specialization) on CareNest!\n\nView profile: $profileUrl"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareMessage)
    }
    context.startActivity(Intent.createChooser(intent, "Share Profile"))
}
