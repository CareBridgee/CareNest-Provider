package com.carenest.provider.profile.presentation.ui.registration

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal enum class NationalIdValidationError {
    REQUIRED,
    INVALID_LENGTH,
    INVALID_NATIONAL_ID,
    INVALID_DATE_OF_BIRTH,
    FUTURE_DATE_OF_BIRTH,
}

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

    fun nationalIdValidationError(
        nationalId: String,
        currentDate: Date = Date(),
    ): NationalIdValidationError? = when {
        nationalId.isBlank() -> NationalIdValidationError.REQUIRED
        nationalId.length != NATIONAL_ID_LENGTH || nationalId.any { !it.isDigit() } ->
            NationalIdValidationError.INVALID_LENGTH
        nationalId.first() !in setOf('2', '3') -> NationalIdValidationError.INVALID_NATIONAL_ID
        extractDateOfBirth(nationalId) == null -> NationalIdValidationError.INVALID_DATE_OF_BIRTH
        isNationalIdDateOfBirthNotInPast(nationalId, currentDate) ->
            NationalIdValidationError.FUTURE_DATE_OF_BIRTH
        else -> null
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

    fun isNationalIdDateOfBirthInFuture(
        nationalId: String,
        currentDate: Date = Date(),
    ): Boolean {
        val dateOfBirth = extractDateOfBirth(nationalId) ?: return false
        return parseDisplayDate(dateOfBirth)?.after(currentDate) == true
    }

    private fun isNationalIdDateOfBirthNotInPast(
        nationalId: String,
        currentDate: Date,
    ): Boolean {
        val dateOfBirth = extractDateOfBirth(nationalId) ?: return false
        return isDateNotInPast(dateOfBirth, currentDate)
    }

    fun extractPastDateOfBirth(
        nationalId: String,
        currentDate: Date = Date(),
    ): String? = extractDateOfBirth(nationalId)?.takeUnless {
        isDateNotInPast(it, currentDate)
    }

    fun toBackendDate(displayDate: String): String {
        val parsedDate = requireNotNull(parseDisplayDate(displayDate)) { "Invalid date of birth" }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsedDate)
    }

    private fun isDateNotInPast(displayDate: String, currentDate: Date): Boolean {
        val dateOfBirth = parseDisplayDate(displayDate) ?: return false
        return !dateOfBirth.before(startOfDay(currentDate))
    }

    private fun startOfDay(date: Date): Date = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    private fun parseDisplayDate(displayDate: String): Date? {
        val sourceFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }
        val position = ParsePosition(0)
        val parsedDate = sourceFormat.parse(displayDate, position) ?: return null
        return parsedDate.takeIf { position.index == displayDate.length }
    }
}
