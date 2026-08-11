package com.carenest.provider.auth

import com.carenest.provider.auth.domain.validation.PhoneNumberValidationError
import com.carenest.provider.auth.domain.validation.PhoneValidator
import com.carenest.provider.auth.domain.validation.SupportedPhoneCountry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhoneValidatorTest {

    @Test
    fun egypt_sanitizesValidatesAndFormatsConventionalNumbers() {
        val country = SupportedPhoneCountry.EGYPT

        assertNull(PhoneValidator.validate("1027642749", country))
        assertEquals("1027642749", PhoneValidator.sanitize("01027642749", country))
        assertEquals("1027642749", PhoneValidator.sanitize("+20 102 764 2749", country))
        assertEquals("102 764 2749", country.format("1027642749"))
        assertEquals("+201027642749", PhoneValidator.toInternationalNumber("1027642749", country))
        assertEquals(
            PhoneNumberValidationError.InvalidFormat,
            PhoneValidator.validate("1327642749", country),
        )
    }

    @Test
    fun saudi_sanitizesValidatesAndFormatsConventionalNumbers() {
        val country = SupportedPhoneCountry.SAUDI_ARABIA

        assertNull(PhoneValidator.validate("501234567", country))
        assertEquals("501234567", PhoneValidator.sanitize("0501234567", country))
        assertEquals("501234567", PhoneValidator.sanitize("+966501234567", country))
        assertEquals("50 123 4567", country.format("501234567"))
        assertEquals(
            PhoneNumberValidationError.InvalidFormat,
            PhoneValidator.validate("401234567", country),
        )
    }

    @Test
    fun uae_acceptsSupportedMobilePrefixesAndRejectsOthers() {
        val country = SupportedPhoneCountry.UAE

        listOf("50", "52", "54", "55", "56", "58").forEach { prefix ->
            assertNull(PhoneValidator.validate(prefix + "1234567", country))
        }
        assertEquals("501234567", PhoneValidator.sanitize("+971501234567", country))
        assertEquals("50 123 4567", country.format("501234567"))
        assertEquals(
            PhoneNumberValidationError.InvalidFormat,
            PhoneValidator.validate("511234567", country),
        )
    }

    @Test
    fun supportedInternationalNumbersNormalizeAndFormatWithoutDuplicatingDialCodes() {
        assertEquals(
            "+201027642749",
            PhoneValidator.normalizeInternationalNumber("+20 102 764 2749"),
        )
        assertEquals(
            "+966 50 123 4567",
            PhoneValidator.formatInternationalNumber("+966501234567"),
        )
        assertEquals(
            "+971 50 123 4567",
            PhoneValidator.formatInternationalNumber("+971501234567"),
        )
    }
}
