package com.carenest.provider.core.navigation


import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

fun SnapshotStateList<NavKey>.navigate(route: NavKey) { //where add nav host in app or core?
    add(route)
}

fun SnapshotStateList<NavKey>.goBack() {
    if (size > 1) {
        removeLastOrNull()
    }
}

fun SnapshotStateList<NavKey>.replaceWith(route: NavKey) {
    Snapshot.withMutableSnapshot {
        clear()
        add(route)
    }
}