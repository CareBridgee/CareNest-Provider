package com.carenest.provider.core.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationExtensionsTest {

    @Test
    fun navigate_addsNewRouteAndIgnoresConsecutiveDuplicate() {
        val backStack = mutableStateListOf<NavKey>(FirstRoute)

        backStack.navigate(SecondRoute)
        backStack.navigate(SecondRoute)

        assertEquals(listOf(FirstRoute, SecondRoute), backStack)
    }

    @Test
    fun goBack_removesTopRouteButKeepsRootRoute() {
        val backStack = mutableStateListOf<NavKey>(FirstRoute, SecondRoute)

        assertTrue(backStack.goBack())
        assertEquals(listOf(FirstRoute), backStack)
        assertFalse(backStack.goBack())
        assertEquals(listOf(FirstRoute), backStack)
    }

    @Test
    fun replaceWith_clearsPreviousFlow() {
        val backStack = mutableStateListOf<NavKey>(FirstRoute, SecondRoute)

        backStack.replaceWith(ThirdRoute)

        assertEquals(listOf(ThirdRoute), backStack)
    }

    @Serializable
    private data object FirstRoute : NavKey

    @Serializable
    private data object SecondRoute : NavKey

    @Serializable
    private data object ThirdRoute : NavKey
}
