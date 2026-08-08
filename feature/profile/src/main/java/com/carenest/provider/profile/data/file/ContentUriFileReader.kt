package com.carenest.provider.profile.data.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.carenest.provider.profile.domain.model.UploadFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ContentUriFileReader @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val resolver: ContentResolver = context.contentResolver

    fun read(uri: Uri, fileName: String, mimeType: String): UploadFile {
        val bytes = resolver.openInputStream(uri)?.use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var total = 0
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                total += count
                require(total <= MAX_FILE_BYTES) { "error_file_too_large" }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        } ?: throw IllegalArgumentException("error_file_open")

        require(bytes.isNotEmpty()) { "error_file_empty" }
        return UploadFile(
            fileName = fileName.ifBlank { "upload" },
            mimeType = mimeType.ifBlank { "application/octet-stream" },
            bytes = bytes,
        )
    }

    private companion object {
        const val MAX_FILE_BYTES = 10 * 1024 * 1024
    }
}
