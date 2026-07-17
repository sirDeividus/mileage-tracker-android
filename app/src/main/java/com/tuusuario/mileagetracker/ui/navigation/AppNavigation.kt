package com.tuusuario.mileagetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuusuario.mileagetracker.ui.history.HistoryScreen
import com.tuusuario.mileagetracker.ui.home.HomeScreen
import com.tuusuario.mileagetracker.ui.summary.SummaryScreen
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.ui.theme.TextMuted



/**
 * AppNavigation.kt
 * -----------------------------------------------------------------------
 * Configura la navegación por pestañas inferiores entre las tres
 * pantallas principales: Inicio, Historial y Resumen. Es el equivalente
 * exacto de AppNavigator.js (que usaba React Navigation), pero con
 * Jetpack Navigation Compose.
 * -----------------------------------------------------------------------
 */
private sealed class Tab(val route: String, val label: String) {
    data object Home : Tab("home", "Inicio")
    data object History : Tab("history", "Historial")
    data object Summary : Tab("summary", "Resumen")
}

private val tabs = listOf(Tab.Home, Tab.History, Tab.Summary)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            val icon = when (tab) {
                                is Tab.Home -> Icons.Default.Home
                                is Tab.History -> Icons.Default.DateRange
                                is Tab.Summary -> Icons.Default.Info
                            }
                            Icon(icon, contentDescription = tab.label)
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Tab.Home.route) { HomeScreen() }
            composable(Tab.History.route) { HistoryScreen() }
            composable(Tab.Summary.route) { SummaryScreen() }
        }
    }
}
