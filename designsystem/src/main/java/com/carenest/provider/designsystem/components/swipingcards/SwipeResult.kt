package com.carenest.provider.designsystem.components.swipingcards

data class SwipeResult<T>(
    val card: T,
    val key: Any,
    val direction: SwipeDirection,
    val resultingOrder: List<T>,
)
