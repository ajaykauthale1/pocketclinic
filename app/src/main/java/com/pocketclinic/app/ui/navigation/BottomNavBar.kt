package com.pocketclinic.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pocketclinic.app.ui.theme.OnSecondaryContainer
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.Surface

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier           = Modifier.height(64.dp),
        containerColor     = Surface,
        tonalElevation     = 0.dp,
        windowInsets       = WindowInsets(0),
    ) {
        NavDestination.bottomNavItems.forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                modifier = Modifier.semantics {
                    contentDescription = destination.contentDescription
                    selected = isSelected
                },
                selected = isSelected,
                onClick  = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon = {
                    // Using text icon as placeholder until vector drawables are added
                    Text(
                        text  = if (isSelected) "●" else "○",
                        color = if (isSelected) PrimaryAmber else OnSecondaryContainer,
                    )
                },
                label = {
                    Text(
                        text  = destination.contentDescription.uppercase(),
                        color = if (isSelected) PrimaryAmber else OnSecondaryContainer,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = PrimaryAmber,
                    unselectedIconColor = OnSecondaryContainer,
                    selectedTextColor   = PrimaryAmber,
                    unselectedTextColor = OnSecondaryContainer,
                    indicatorColor      = Color.Transparent,
                ),
            )
        }
    }
}
