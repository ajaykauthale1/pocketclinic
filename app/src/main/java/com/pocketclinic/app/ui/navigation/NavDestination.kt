package com.pocketclinic.app.ui.navigation

import androidx.annotation.StringRes
import com.pocketclinic.app.R

sealed class NavDestination(
    val route: String,
    val contentDescription: String,
    val icon: String,          // Material symbol name
    val iconFilled: String,    // Filled variant
) {
    data object Command : NavDestination(
        route              = "command",
        contentDescription = "Command",
        icon               = "grid_view",
        iconFilled         = "grid_view",
    )

    data object Tests : NavDestination(
        route              = "tests",
        contentDescription = "Tests",
        icon               = "biotech",
        iconFilled         = "biotech",
    )

    data object Reports : NavDestination(
        route              = "reports",
        contentDescription = "Reports",
        icon               = "description",
        iconFilled         = "description",
    )

    data object Profile : NavDestination(
        route              = "profile",
        contentDescription = "Profile",
        icon               = "person_2",
        iconFilled         = "person_2",
    )

    companion object {
        val bottomNavItems = listOf(Command, Tests, Reports, Profile)
    }
}
