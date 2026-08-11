package com.carenest.provider.auth.domain.validation

enum class PhoneNumberValidationError {
    Required,
    InvalidLength,
    InvalidFormat,
}

enum class SupportedPhoneCountry(
    val dialCode: String,
    val nationalDigitLength: Int,
    val groupSizes: List<Int>,
    private val mobileRegex: Regex,
) {
    EGYPT(
        dialCode = "+20",
        nationalDigitLength = 10,
        groupSizes = listOf(3, 3, 4),
        mobileRegex = Regex("^1[0125][0-9]{8}$"),
    ),
    SAUDI_ARABIA(
        dialCode = "+966",
        nationalDigitLength = 9,
        groupSizes = listOf(2, 3, 4),
        mobileRegex = Regex("^5[0-9]{8}$"),
    ),
    UAE(
        dialCode = "+971",
        nationalDigitLength = 9,
        groupSizes = listOf(2, 3, 4),
        mobileRegex = Regex("^5[024568][0-9]{7}$"),
    );

    fun sanitize(input: String): String {
        var digits = input.filter(Char::isDigit)
        val dialDigits = dialCode.filter(Char::isDigit)

        if (digits.startsWith(dialDigits) && digits.length > nationalDigitLength) {
            digits = digits.drop(dialDigits.length)
        }
        if (digits.startsWith('0') && digits.length > nationalDigitLength) {
            digits = digits.drop(1)
        }

        return digits.take(nationalDigitLength)
    }

    fun validate(nationalDigits: String): PhoneNumberValidationError? = when {
        nationalDigits.isBlank() -> PhoneNumberValidationError.Required
        nationalDigits.length != nationalDigitLength -> PhoneNumberValidationError.InvalidLength
        !mobileRegex.matches(nationalDigits) -> PhoneNumberValidationError.InvalidFormat
        else -> null
    }

    fun format(nationalDigits: String): String {
        val digits = nationalDigits.filter(Char::isDigit).take(nationalDigitLength)
        var offset = 0
        return buildList {
            groupSizes.forEach { groupSize ->
                if (offset >= digits.length) return@forEach
                val end = (offset + groupSize).coerceAtMost(digits.length)
                add(digits.substring(offset, end))
                offset = end
            }
        }.joinToString(" ")
    }

    fun toInternationalNumber(nationalDigits: String): String = dialCode + nationalDigits
}

object PhoneValidator {
    fun sanitize(input: String, country: SupportedPhoneCountry): String = country.sanitize(input)

    fun validate(
        nationalDigits: String,
        country: SupportedPhoneCountry,
    ): PhoneNumberValidationError? = country.validate(nationalDigits)

    fun toInternationalNumber(
        nationalDigits: String,
        country: SupportedPhoneCountry,
    ): String = country.toInternationalNumber(nationalDigits)

    fun detectCountry(input: String): SupportedPhoneCountry? {
        val digits = input.filter(Char::isDigit)
        return SupportedPhoneCountry.entries.firstOrNull { country ->
            digits.startsWith(country.dialCode.filter(Char::isDigit))
        } ?: SupportedPhoneCountry.EGYPT.takeIf { digits.startsWith('0') }
    }

    fun formatInternationalNumber(input: String): String {
        val country = detectCountry(input) ?: return input
        val nationalDigits = country.sanitize(input)
        return "${country.dialCode} ${country.format(nationalDigits)}"
    }

    fun normalizeInternationalNumber(input: String): String? {
        val country = detectCountry(input) ?: return null
        val nationalDigits = country.sanitize(input)
        return country.toInternationalNumber(nationalDigits)
            .takeIf { country.validate(nationalDigits) == null }
    }
}
