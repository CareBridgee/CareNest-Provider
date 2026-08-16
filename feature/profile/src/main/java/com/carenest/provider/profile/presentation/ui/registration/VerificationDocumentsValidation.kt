package com.carenest.provider.profile.presentation.ui.registration

internal object VerificationDocumentsValidation {
    const val MIN_YEARS_OF_EXPERIENCE = 0
    const val MAX_YEARS_OF_EXPERIENCE = 90
    const val MIN_SPECIALITY_LENGTH = 3
    const val MAX_SPECIALITY_LENGTH = 50
    const val MIN_LICENSE_NUMBER_LENGTH = 5
    const val MAX_LICENSE_NUMBER_LENGTH = 20

    private val supportedSpecialityCharacters = Regex("^[\\p{L}\\p{M} &'/-]+$")
    private val excessiveCharacterRepetition = Regex("(.)\\1{3,}", RegexOption.IGNORE_CASE)
    private val licenseNumberFormat = Regex("^[A-Za-z0-9][A-Za-z0-9/-]*$")

    fun isValidYearsOfExperience(years: Int): Boolean =
        years in MIN_YEARS_OF_EXPERIENCE..MAX_YEARS_OF_EXPERIENCE

    fun isValidPrimarySpeciality(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.length in MIN_SPECIALITY_LENGTH..MAX_SPECIALITY_LENGTH &&
            supportedSpecialityCharacters.matches(trimmed) &&
            trimmed.count(Char::isLetter) >= MIN_SPECIALITY_LENGTH &&
            !excessiveCharacterRepetition.containsMatchIn(trimmed)
    }

    fun isValidLicenseNumber(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.length in MIN_LICENSE_NUMBER_LENGTH..MAX_LICENSE_NUMBER_LENGTH &&
            licenseNumberFormat.matches(trimmed)
    }
}
