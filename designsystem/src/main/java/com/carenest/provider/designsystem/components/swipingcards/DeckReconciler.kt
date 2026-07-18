package com.carenest.provider.designsystem.components.swipingcards

internal object DeckReconciler {

    fun requireUniqueKeys(keys: List<Any>) {
        val seen = HashSet<Any>(keys.size)
        for (key in keys) {
            require(seen.add(key)) {
                "SwipingCardStack: duplicate card key '$key'. Keys returned by `key` must be " +
                    "unique within the supplied list."
            }
        }
    }

    fun rotateFrontToBack(order: List<Any>): List<Any> {
        if (order.size <= 1) return order
        return order.drop(1) + order.first()
    }

    fun reconcile(currentOrder: List<Any>, externalKeys: List<Any>): ReconcileResult {
        val externalSet = externalKeys.toHashSet()
        val currentSet = currentOrder.toHashSet()

        val removed = currentSet - externalSet
        val added = externalSet - currentSet

        val retained = currentOrder.filter { it in externalSet }

        val externalOrder = externalKeys.filter { it in added }
        val newOrder = retained + externalOrder

        return ReconcileResult(newOrder = newOrder, added = added, removed = removed)
    }
}

data class ReconcileResult(
    val newOrder: List<Any>,
    val added: Set<Any>,
    val removed: Set<Any>,
)
