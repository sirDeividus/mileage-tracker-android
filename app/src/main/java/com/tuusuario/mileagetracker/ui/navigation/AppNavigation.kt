package com.tuusuario.mileagetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
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
import com.tuusuario.mileagetracker.data.local.AppLanguage
import com.tuusuario.mileagetracker.data.local.ThemeMode
import com.tuusuario.mileagetracker.ui.history.HistoryScreen
import com.tuusuario.mileagetracker.ui.home.HomeScreen
import com.tuusuario.mileagetracker.ui.settings.SettingsScreen
import com.tuusuario.mileagetracker.ui.summary.SummaryScreen
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.util.LocalAppStrings

/**
 * AppNavigation.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Configura la navegación por pestañas inferiores. Se agregó una cuarta
 * pestaña, "Ajustes", donde el usuario elige idioma, tema y su estado de
 * EE.UU. Los cambios de idioma/tema se manejan "hacia arriba" en
 * MainActivity (single source of truth), por eso esta función recibe
 * los valores actuales y los callbacks para cambiarlos.
 * -----------------------------------------------------------------------
 */
private sealed class Tab(val route: String) {
    data object Home : Tab("home")
    data object History : Tab("history")
    data object Summary : Tab("summary")
    data object Settings : Tab("settings")
}

private val tabs = listOf(Tab.Home, Tab.History, Tab.Summary, Tab.Settings)

@Composable
fun AppNavigation(
    currentLanguage: AppLanguage,
    currentThemeMode: ThemeMode,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    fun labelFor(tab: Tab): String = when (tab) {
        is Tab.Home -> strings.tabHome
        is Tab.History -> strings.tabHistory
        is Tab.Summary -> strings.tabSummary
        is Tab.Settings -> strings.tabSettings
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = appColors.surface) {
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
                                is Tab.Settings -> Icons.Default.Settings
                            }
                            Icon(icon, contentDescription = labelFor(tab))
                        },
                        label = { Text(labelFor(tab)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            unselectedIconColor = appColors.textMuted,
                            unselectedTextColor = appColors.textMuted,
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
            composable(Tab.Settings.route) {
                SettingsScreen(
                    currentLanguage = currentLanguage,
                    currentThemeMode = currentThemeMode,
                    onLanguageChange = onLanguageChange,
                    onThemeModeChange = onThemeModeChange,
                )
            }
        }
    }
}
