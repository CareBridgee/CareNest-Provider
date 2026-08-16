package com.carenest.provider.profile

import com.carenest.provider.profile.presentation.ui.registration.VerificationDocumentsValidation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VerificationDocumentsValidationTest {

    @Test
    fun yearsOfExperienceMustBeWithinReasonableRange() {
        assertTrue(VerificationDocumentsValidation.isValidYearsOfExperience(0))
        assertTrue(VerificationDocumentsValidation.isValidYearsOfExperience(70))
        assertFalse(VerificationDocumentsValidation.isValidYearsOfExperience(71))
        assertFalse(VerificationDocumentsValidation.isValidYearsOfExperience(800000))
    }

    @Test
    fun primarySpecialityRequiresReasonableLengthAndCharacters() {
        assertTrue(VerificationDocumentsValidation.isValidPrimarySpeciality("ICU Nursing"))
        assertTrue(VerificationDocumentsValidation.isValidPrimarySpeciality("تمريض أطفال"))
        assertFalse(VerificationDocumentsValidation.isValidPrimarySpeciality("n"))
        assertFalse(
            VerificationDocumentsValidation.isValidPrimarySpeciality(
                "vvvvv cm kg cm kg do go to go to to gtttt",
            ),
        )
        assertFalse(VerificationDocumentsValidation.isValidPrimarySpeciality("A".repeat(51)))
        assertFalse(VerificationDocumentsValidation.isValidPrimarySpeciality("ICU_123"))
    }

    @Test
    fun licenseNumberRequiresSupportedFormatAndLength() {
        assertTrue(VerificationDocumentsValidation.isValidLicenseNumber("RN-12345"))
        assertTrue(VerificationDocumentsValidation.isValidLicenseNumber("12345"))
        assertFalse(VerificationDocumentsValidation.isValidLicenseNumber("0"))
        assertFalse(
            VerificationDocumentsValidation.isValidLicenseNumber(
                "88777655543333222211145667899999999",
            ),
        )
        assertFalse(VerificationDocumentsValidation.isValidLicenseNumber("RN 12345"))
    }
}
