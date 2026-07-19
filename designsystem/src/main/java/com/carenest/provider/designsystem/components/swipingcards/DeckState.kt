package com.carenest.provider.designsystem.components.swipingcards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

private const val SETTLE_SPRING_DAMPING = 0.75f
private const val SETTLE_SPRING_STIFFNESS = 300f
private const val PROMOTE_SPRING_DAMPING = 0.7f
private const val PROMOTE_SPRING_STIFFNESS = 80f

private const val BACKGROUND_VERTICAL_REPULSION_FACTOR = 0.35f
private const val BACKGROUND_HORIZONTAL_REPULSION_FACTOR = 0.25f

private const val DEG_TO_RAD = PI / 180.0

internal data class StackPositionConfig(
    val scale: Float,
    val rotationZ: Float,
    val alpha: Float,
    val repulsionFactor: Float,
    val elevation: Dp,
)

internal fun stackPositionConfig(position: Int): StackPositionConfig = when (position) {
    0 -> StackPositionConfig(scale = 1.0f, rotationZ = 0f, alpha = 1f, repulsionFactor = 0f, elevation = 6.dp)
    1 -> StackPositionConfig(scale = 0.96f, rotationZ = -2.5f, alpha = 1f, repulsionFactor = 0.7f, elevation = 4.dp)
    2 -> StackPositionConfig(scale = 0.92f, rotationZ = 2f, alpha = 0.96f, repulsionFactor = 0.4f, elevation = 2.dp)
    3 -> StackPositionConfig(scale = 0.88f, rotationZ = -1f, alpha = 0.92f, repulsionFactor = 0.2f, elevation = 1.dp)
    else -> StackPositionConfig(scale = 0.84f, rotationZ = 1f, alpha = 0.88f, repulsionFactor = 0.2f, elevation = 1.dp)
}

internal fun idleTranslationXPx(
    position: Int,
    scale: Float,
    rotationZDeg: Float,
    cardWidthPx: Float,
): Float {
    if (position == 0) return 0f
    val cosR = cos(rotationZDeg * DEG_TO_RAD).toFloat()
    val halfW = cardWidthPx / 2f
    return if ((position % 2) == 1) {
        -halfW + (halfW * scale) * cosR
    } else {
        halfW - (halfW * scale) * cosR
    }
}

@Stable
internal class CardAnimState(config: StackPositionConfig, initialXPx: Float) {
    val scale = Animatable(config.scale)
    val rotationZ = Animatable(config.rotationZ)
    val translationX = Animatable(initialXPx)
    val translationY = Animatable(0f)
    val alpha = Animatable(config.alpha)
    val elevation = Animatable(config.elevation.value)

    var repulsionX by mutableFloatStateOf(initialXPx)
    var repulsionY by mutableFloatStateOf(0f)
    var isDragging by mutableStateOf(value = false)
}

@Stable
internal class DeckState {

    var internalOrder by mutableStateOf<List<Any>>(emptyList())
        private set

    var swipedKey by mutableStateOf<Any?>(null)
        private set

    var isAnimating by mutableStateOf(false)
    var hasPassedThreshold by mutableStateOf(false)

    var maxVisibleCards by mutableIntStateOf(4)
    var maxRotationY by mutableFloatStateOf(38f)
    var swipeThresholdFraction by mutableFloatStateOf(0.20f)

    var containerWidthPx by mutableFloatStateOf(0f)
    var containerHeightPx by mutableFloatStateOf(0f)

    var dragX by mutableFloatStateOf(0f)
    var dragY by mutableFloatStateOf(0f)
    var rotationY by mutableFloatStateOf(0f)

    private val animStates = mutableStateListOf<Pair<Any, CardAnimState>>()

    fun init(keys: List<Any>) {
        internalOrder = keys
        animStates.clear()
        keys.forEachIndexed { index, key ->
            animStates.add(key to cardAnimStateForPosition(index))
        }
    }

    fun animStateFor(key: Any): CardAnimState? =
        animStates.firstOrNull { it.first == key }?.second

    private fun cardAnimStateForPosition(position: Int): CardAnimState {
        val cfg = stackPositionConfig(position)
        val idleX = idleTranslationXPx(
            position = position,
            scale = cfg.scale,
            rotationZDeg = cfg.rotationZ,
            cardWidthPx = containerWidthPx,
        )
        return CardAnimState(cfg, idleX)
    }

    fun applyReconcile(result: ReconcileResult) {
        internalOrder = result.newOrder
        result.removed.forEach { key -> animStates.removeAll { it.first == key } }
        result.added.forEach { key ->
            val position = result.newOrder.indexOf(key)
            if (position >= 0) {
                animStates.add(key to cardAnimStateForPosition(position))
            }
        }
    }

    fun onDrag(dx: Float, dy: Float) {
        dragX += dx
        dragY += dy

        val swipeThresholdPx = containerWidthPx * swipeThresholdFraction
        rotationY = (dragX / swipeThresholdPx) * maxRotationY

        hasPassedThreshold =
            abs(dragX) >= swipeThresholdPx ||
                abs(dragY) >= containerHeightPx * swipeThresholdFraction

        val dragMagnitude = sqrt(dragX * dragX + dragY * dragY)
        internalOrder.drop(1).forEachIndexed { idx, key ->
            val pos = idx + 1
            val cfg = stackPositionConfig(pos)
            animStateFor(key)?.let { anim ->
                val idleX = idleTranslationXPx(pos, cfg.scale, cfg.rotationZ, containerWidthPx)
                val repelX = dragX * cfg.repulsionFactor * BACKGROUND_HORIZONTAL_REPULSION_FACTOR
                val repelY = -dragMagnitude * cfg.repulsionFactor * BACKGROUND_VERTICAL_REPULSION_FACTOR
                anim.repulsionX = idleX + repelX
                anim.repulsionY = repelY
                anim.isDragging = true
            }
        }
    }

    suspend fun settleBack() {
        val topKey = internalOrder.firstOrNull() ?: return
        val topAnim = animStateFor(topKey) ?: return
        val settleSpec = spring<Float>(dampingRatio = SETTLE_SPRING_DAMPING, stiffness = SETTLE_SPRING_STIFFNESS)

        val currentDragX = dragX
        val currentDragY = dragY

        dragX = 0f
        dragY = 0f
        rotationY = 0f
        hasPassedThreshold = false

        coroutineScope {

            launch {
                topAnim.translationX.snapTo(topAnim.translationX.value + currentDragX)
                topAnim.translationY.snapTo(topAnim.translationY.value + currentDragY)
                topAnim.translationX.animateTo(0f, settleSpec)
                topAnim.translationY.animateTo(0f, settleSpec)
                topAnim.elevation.animateTo(stackPositionConfig(0).elevation.value, settleSpec)
            }

            internalOrder.drop(1).forEachIndexed { idx, key ->
                val pos = idx + 1
                val cfg = stackPositionConfig(pos)
                animStateFor(key)?.let { bgAnim ->
                    val idleX = idleTranslationXPx(pos, cfg.scale, cfg.rotationZ, containerWidthPx)
                    launch {

                        if (bgAnim.isDragging) {
                            bgAnim.translationX.snapTo(bgAnim.repulsionX)
                            bgAnim.translationY.snapTo(bgAnim.repulsionY)
                            bgAnim.isDragging = false
                        }
                        bgAnim.translationX.animateTo(idleX, settleSpec)
                        bgAnim.translationY.animateTo(0f, settleSpec)
                    }
                }
            }
        }
    }

    suspend fun commitSwipe(
        scope: CoroutineScope,
        direction: SwipeDirection,
        onRotate: () -> Unit = {},
    ) {
        isAnimating = true

        val topKey = internalOrder.firstOrNull() ?: run { isAnimating = false; return }
        val topAnim = animStateFor(topKey) ?: run { isAnimating = false; return }

        swipedKey = topKey

        val currentDragX = dragX
        val currentDragY = dragY

        val flyX = when (direction) {
            SwipeDirection.Left -> -containerWidthPx * 1.5f
            SwipeDirection.Right -> containerWidthPx * 1.5f
            else -> dragX
        }
        val flyY = when (direction) {
            SwipeDirection.Up -> -containerHeightPx * 1.5f
            SwipeDirection.Down -> containerHeightPx * 1.5f
            else -> dragY
        }

        val flySpec = spring<Float>(dampingRatio = 1.0f, stiffness = 500f)

        topAnim.translationX.snapTo(topAnim.translationX.value + currentDragX)
        topAnim.translationY.snapTo(topAnim.translationY.value + currentDragY)

        dragX = 0f
        dragY = 0f
        rotationY = 0f
        hasPassedThreshold = false

        scope.launch {
            coroutineScope {
                launch { topAnim.translationX.animateTo(flyX, flySpec) }
                launch { topAnim.translationY.animateTo(flyY, flySpec) }
                launch { topAnim.alpha.animateTo(0f, flySpec) }
            }
        }

        val newOrder = DeckReconciler.rotateFrontToBack(internalOrder)
        internalOrder = newOrder
        onRotate()

        val backPosition = newOrder.lastIndex
        val backCfg = stackPositionConfig(backPosition)
        val backIdleX = idleTranslationXPx(backPosition, backCfg.scale, backCfg.rotationZ, containerWidthPx)

        topAnim.scale.snapTo(backCfg.scale)
        topAnim.rotationZ.snapTo(backCfg.rotationZ)
        topAnim.elevation.snapTo(backCfg.elevation.value)
        topAnim.translationX.snapTo(backIdleX + flyX)
        topAnim.translationY.snapTo(flyY)
        topAnim.alpha.snapTo(0f)

        val promoteSpec = spring<Float>(dampingRatio = PROMOTE_SPRING_DAMPING, stiffness = PROMOTE_SPRING_STIFFNESS)

        coroutineScope {
            newOrder.forEachIndexed { index, key ->
                val cfg = stackPositionConfig(index)
                val idleX = idleTranslationXPx(index, cfg.scale, cfg.rotationZ, containerWidthPx)
                animStateFor(key)?.let { anim ->

                    if (anim.isDragging) {
                        anim.translationX.snapTo(anim.repulsionX)
                        anim.translationY.snapTo(anim.repulsionY)
                        anim.isDragging = false
                    }
                    launch { anim.scale.animateTo(cfg.scale, promoteSpec) }
                    launch { anim.rotationZ.animateTo(cfg.rotationZ, promoteSpec) }
                    launch { anim.translationX.animateTo(idleX, promoteSpec) }
                    launch { anim.translationY.animateTo(0f, promoteSpec) }
                    launch { anim.alpha.animateTo(cfg.alpha, promoteSpec) }
                    launch { anim.elevation.animateTo(cfg.elevation.value, promoteSpec) }
                }
            }
        }

        swipedKey = null
        isAnimating = false
    }
}
