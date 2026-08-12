package com.carenest.provider.auth

import androidx.compose.ui.text.AnnotatedString
import com.carenest.provider.auth.presentation.auth.login.components.PhoneNumberVisualTransformation
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberVisualTransformationTest {

    @Test
    fun formattingKeepsCursorOffsetsStableAroundInsertedSpaces() {
        val transformed = PhoneNumberVisualTransformation(listOf(3, 3, 4))
            .filter(AnnotatedString("1027642749"))

        assertEquals("102 764 2749", transformed.text.text)
        assertEquals(4, transformed.offsetMapping.originalToTransformed(3))
        assertEquals(3, transformed.offsetMapping.transformedToOriginal(3))
        assertEquals(3, transformed.offsetMapping.transformedToOriginal(4))
        assertEquals(10, transformed.offsetMapping.transformedToOriginal(12))
    }
}
