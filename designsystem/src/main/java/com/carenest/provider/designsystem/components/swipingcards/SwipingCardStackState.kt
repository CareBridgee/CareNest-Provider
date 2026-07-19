package com.carenest.provider.designsystem.components.swipingcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSwipingCardStackState(): SwipingCardStackState {
    val scope = rememberCoroutineScope()
    return remember(scope) { SwipingCardStackState(scope) }
}

class SwipingCardStackState(
    private val scope: CoroutineScope
) {
    internal var deck: DeckState? = null
    internal var onSwipeTrigger: ((SwipeDirection) -> Unit)? = null

    val isAnimating: Boolean
        get() = deck?.isAnimating ?: false

    fun swipe(direction: SwipeDirection = SwipeDirection.Right) {
        val d = deck ?: return
        if (d.isAnimating) return

        scope.launch {
            d.commitSwipe(
                scope = this,
                direction = direction,
                onRotate = { onSwipeTrigger?.invoke(direction) }
            )
        }
    }
}
