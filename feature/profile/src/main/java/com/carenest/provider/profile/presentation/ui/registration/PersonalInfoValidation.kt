package com.carenest.provider.profile.presentation.ui.registration

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

internal object PersonalInfoValidation {
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 50
    const val NATIONAL_ID_LENGTH = 14

    private val supportedNameCharacters = Regex("^[\\p{L}\\p{M} '\\-]+$")

    fun sanitizeName(value: String): String = value
        .filter { character ->
            character.isLetter() ||
                character.category == CharCategory.NON_SPACING_MARK ||
                character.category == CharCategory.COMBINING_SPACING_MARK ||
                character == ' ' || character == '-' || character == '\''
        }
        .take(MAX_NAME_LENGTH)

    fun isValidName(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH &&
            supportedNameCharacters.matches(trimmed) &&
            trimmed.count(Char::isLetter) >= MIN_NAME_LENGTH
    }

    fun normalizeNationalId(value: String): String = buildString {
        value.forEach { character ->
            val digit = Character.digit(character, 10)
            if (digit >= 0 && length < NATIONAL_ID_LENGTH) append(('0'.code + digit).toChar())
        }
    }

    /** Returns the date encoded in an Egyptian national ID as DD/MM/YYYY. */
    fun extractDateOfBirth(nationalId: String): String? {
        if (nationalId.length != NATIONAL_ID_LENGTH || nationalId.any { !it.isDigit() }) return null

        val century = when (nationalId.first()) {
            '2' -> "19"
            '3' -> "20"
            else -> return null
        }
        val encodedDate = century + nationalId.substring(1, 7)
        val sourceFormat = SimpleDateFormat("yyyyMMdd", Locale.US).apply { isLenient = false }
        val position = ParsePosition(0)
        val parsedDate = sourceFormat.parse(encodedDate, position) ?: return null
        if (position.index != encodedDate.length) return null

        return SimpleDateFormat("dd/MM/yyyy", Locale.US).format(parsedDate)
    }

    fun toBackendDate(displayDate: String): String {
        val sourceFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }
        val position = ParsePosition(0)
        val parsedDate = sourceFormat.parse(displayDate, position)
        require(parsedDate != null && position.index == displayDate.length) { "Invalid date of birth" }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsedDate)
    }
}
