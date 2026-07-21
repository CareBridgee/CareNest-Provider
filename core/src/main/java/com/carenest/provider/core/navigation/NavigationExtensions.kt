package com.carenest.provider.core.navigation

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

fun SnapshotStateList<NavKey>.navigate(route: NavKey) {
    if (lastOrNull() != route) {
        add(route)
    }
}

fun SnapshotStateList<NavKey>.goBack(): Boolean {
    return if (size > 1) {
        removeLastOrNull()
        true
    } else {
        false
    }
}

fun SnapshotStateList<NavKey>.replaceWith(route: NavKey) {
    Snapshot.withMutableSnapshot {
        clear()
        add(route)
    }
}
