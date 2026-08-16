package com.carenest.provider.profile

import com.carenest.provider.profile.presentation.ui.registration.PersonalInfoValidation
import com.carenest.provider.profile.presentation.ui.registration.NationalIdValidationError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class PersonalInfoValidationTest {

    @Test
    fun extractsDateOfBirthFromNationalIdInDayMonthYearOrder() {
        assertEquals(
            "01/01/2003",
            PersonalInfoValidation.extractDateOfBirth("30301010105222"),
        )
        assertEquals(
            "29/02/2000",
            PersonalInfoValidation.extractDateOfBirth("30002290105222"),
        )
    }

    @Test
    fun rejectsNationalIdsWithAnInvalidEncodedDate() {
        assertNull(PersonalInfoValidation.extractDateOfBirth("30302300105222"))
        assertNull(PersonalInfoValidation.extractDateOfBirth("40301010105222"))
        assertNull(PersonalInfoValidation.extractDateOfBirth("3030101010522"))
    }

    @Test
    fun reportsTheSpecificNationalIdProblem() {
        assertEquals(
            NationalIdValidationError.INVALID_LENGTH,
            PersonalInfoValidation.nationalIdValidationError("3027417260174"),
        )
        assertEquals(
            NationalIdValidationError.INVALID_DATE_OF_BIRTH,
            PersonalInfoValidation.nationalIdValidationError("30274172601749"),
        )
        assertEquals(
            NationalIdValidationError.INVALID_NATIONAL_ID,
            PersonalInfoValidation.nationalIdValidationError("40201012601749"),
        )
    }

    @Test
    fun identifiesAndDoesNotAutoPopulateFutureDateOfBirth() {
        val referenceDate = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("11/05/2027")!!
        val nationalIdWithFutureDate = "32705129999999"

        assertTrue(
            PersonalInfoValidation.isNationalIdDateOfBirthInFuture(
                nationalIdWithFutureDate,
                referenceDate,
            ),
        )
        assertNull(PersonalInfoValidation.extractPastDateOfBirth(nationalIdWithFutureDate, referenceDate))
    }

    @Test
    fun convertsDisplayedDateToBackendFormat() {
        assertEquals("2003-01-31", PersonalInfoValidation.toBackendDate("31/01/2003"))
    }

    @Test
    fun validatesNameLengthAndSupportedCharacters() {
        assertTrue(PersonalInfoValidation.isValidName("حسن"))
        assertTrue(PersonalInfoValidation.isValidName("Anne-Marie"))
        assertFalse(PersonalInfoValidation.isValidName("ح"))
        assertFalse(PersonalInfoValidation.isValidName("||||"))
        assertFalse(PersonalInfoValidation.isValidName("a".repeat(51)))
    }

    @Test
    fun sanitizesNamesAndArabicNationalIdDigitsAtInputBoundary() {
        assertEquals("Sara", PersonalInfoValidation.sanitizeName("Sa|ra123"))
        assertEquals(
            "30301010105222",
            PersonalInfoValidation.normalizeNationalId("٣٠٣٠١٠١٠١٠٥٢٢٢99"),
        )
    }
}
