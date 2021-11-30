package dev.katiebarnett.decktagram.util

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.navigateSafe(
    @IdRes resId: Int,
    action: NavDirections
) {
    // Check to make sure our current position is what we expect
    //  this is needed in the case the user makes navigation actions very quick together
    if (currentDestination?.id == resId) {
        navigate(action)
    }
}