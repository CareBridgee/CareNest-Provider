package com.carenest.provider.profile

import com.carenest.provider.profile.presentation.ui.registration.PersonalInfoValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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
