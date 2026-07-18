package com.carenest.provider.designsystem.components.swipingcards

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

private const val FLING_VELOCITY = 2000f
private const val CAMERA_DISTANCE = 12f

@SuppressLint("RememberReturnType")
@Composable
fun <T> SwipingCardStack(
    cards: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    state: SwipingCardStackState = rememberSwipingCardStackState(),
    maxVisibleCards: Int = 4,
    maxRotationY: Float = 38f,
    swipeThresholdFraction: Float = 0.20f,
    onSwipe: (SwipeResult<T>) -> Unit = {},
    cardContent: @Composable (T) -> Unit,
) {
    require(maxVisibleCards >= 1) {
        "SwipingCardStack: maxVisibleCards must be >= 1 but was $maxVisibleCards."
    }

    val externalKeys = remember(cards) {
        cards.map(key).also(DeckReconciler::requireUniqueKeys)
    }
    val cardsByKey: Map<Any, T> = remember(cards) { cards.associateBy(key) }

    val deck = remember { DeckState() }
    deck.maxVisibleCards = maxVisibleCards
    deck.maxRotationY = maxRotationY
    deck.swipeThresholdFraction = swipeThresholdFraction

    state.deck = deck

    remember(externalKeys) {
        if (deck.internalOrder.isEmpty()) {
            deck.init(externalKeys)
        } else {
            val result = DeckReconciler.reconcile(deck.internalOrder, externalKeys)
            deck.applyReconcile(result)
        }
    }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val onSwipeState = rememberUpdatedState(onSwipe)

    state.onSwipeTrigger = { direction ->

        val swipedKey = deck.internalOrder.lastOrNull()
        val swipedCard = swipedKey?.let { cardsByKey[it] }
        if (swipedCard != null) {
            val resultingCards = deck.internalOrder.mapNotNull { k -> cardsByKey[k] }
            onSwipeState.value(
                SwipeResult(
                    card = swipedCard,
                    key = swipedKey,
                    direction = direction,
                    resultingOrder = resultingCards,
                )
            )
        }
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        deck.containerWidthPx = widthPx
        deck.containerHeightPx = heightPx

        val swipedKey = deck.swipedKey
        val visibleKeys = deck.internalOrder.take(maxVisibleCards)
        val cardWidthDp: Dp = maxWidth
        val cardHeightDp: Dp = maxHeight

        visibleKeys.forEachIndexed { stackIndex, cardKey ->

            if (cardKey == swipedKey) return@forEachIndexed

            key(cardKey) {
                val isTopCard = stackIndex == 0
                val animState = deck.animStateFor(cardKey)
                if (animState != null) {
                    val cameraDistancePx = CAMERA_DISTANCE * density.density * 160f

                    Box(
                        modifier = Modifier
                            .size(cardWidthDp, cardHeightDp)
                            .zIndex((maxVisibleCards - stackIndex).toFloat())
                            .graphicsLayer {
                                scaleX = animState.scale.value
                                scaleY = animState.scale.value
                                this.rotationZ = animState.rotationZ.value

                                translationX = if (!isTopCard && animState.isDragging) {
                                    animState.repulsionX
                                } else {
                                    animState.translationX.value
                                }
                                translationY = if (!isTopCard && animState.isDragging) {
                                    animState.repulsionY
                                } else {
                                    animState.translationY.value
                                }
                                alpha = animState.alpha.value
                                if (isTopCard) {
                                    rotationY = deck.rotationY
                                    cameraDistance = cameraDistancePx
                                    transformOrigin = TransformOrigin(
                                        pivotFractionX = if (deck.rotationY > 0f) 0f else 1f,
                                        pivotFractionY = 0.5f,
                                    )
                                    translationX += deck.dragX
                                    translationY += deck.dragY
                                }
                                shadowElevation = animState.elevation.value.dp.toPx()
                            }
                            .then(
                                if (isTopCard && !deck.isAnimating) {
                                    Modifier.pointerInput(cardKey) {
                                        val velocityTracker = VelocityTracker()
                                        var hapticFired = false

                                        detectDragGestures(
                                            onDragStart = {
                                                hapticFired = false
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                velocityTracker.addPosition(
                                                    change.uptimeMillis,
                                                    change.position,
                                                )
                                                deck.onDrag(dragAmount.x, dragAmount.y)

                                                if (deck.hasPassedThreshold && !hapticFired) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    hapticFired = true
                                                } else if (!deck.hasPassedThreshold) {
                                                    hapticFired = false
                                                }
                                            },
                                            onDragEnd = {
                                                val velocity = velocityTracker.calculateVelocity()
                                                val vx = velocity.x
                                                val vy = velocity.y
                                                val speed = sqrt(vx * vx + vy * vy)

                                                val shouldCommit = deck.hasPassedThreshold || speed >= FLING_VELOCITY
                                                if (shouldCommit) {
                                                    val direction = dominantDirection(deck.dragX, deck.dragY, vx, vy)
                                                    val swipedCard = cardsByKey[cardKey]
                                                    if (swipedCard != null) {
                                                        scope.launch {
                                                            deck.commitSwipe(
                                                                scope = this,
                                                                direction = direction,
                                                            ) {
                                                                val resultingCards = deck.internalOrder
                                                                    .mapNotNull { k -> cardsByKey[k] }
                                                                onSwipeState.value(
                                                                    SwipeResult(
                                                                        card = swipedCard,
                                                                        key = cardKey,
                                                                        direction = direction,
                                                                        resultingOrder = resultingCards,
                                                                    )
                                                                )
                                                            }
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        }
                                                    }
                                                } else {
                                                    scope.launch { deck.settleBack() }
                                                }
                                            },
                                            onDragCancel = {
                                                scope.launch { deck.settleBack() }
                                            },
                                        )
                                    }
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        val item = cardsByKey[cardKey]
                        if (item != null) {
                            cardContent(item)
                        }
                    }
                }
            }
        }

        if (swipedKey != null) {
            key(swipedKey) {
                val animState = deck.animStateFor(swipedKey)
                if (animState != null) {
                    Box(
                        modifier = Modifier
                            .size(cardWidthDp, cardHeightDp)
                            .zIndex(maxVisibleCards + 1f)
                            .graphicsLayer {
                                scaleX = animState.scale.value
                                scaleY = animState.scale.value
                                rotationZ = animState.rotationZ.value
                                translationX = animState.translationX.value
                                translationY = animState.translationY.value
                                alpha = animState.alpha.value
                                shadowElevation = animState.elevation.value.dp.toPx()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        val item = cardsByKey[swipedKey]
                        if (item != null) {
                            cardContent(item)
                        }
                    }
                }
            }
        }
    }
}

private fun dominantDirection(
    dragX: Float,
    dragY: Float,
    velocityX: Float,
    velocityY: Float,
): SwipeDirection {
    val dx = if (abs(velocityX) > FLING_VELOCITY) velocityX else dragX
    val dy = if (abs(velocityY) > FLING_VELOCITY) velocityY else dragY
    return if (abs(dx) >= abs(dy)) {
        if (dx > 0f) SwipeDirection.Right else SwipeDirection.Left
    } else {
        if (dy > 0f) SwipeDirection.Down else SwipeDirection.Up
    }
}
