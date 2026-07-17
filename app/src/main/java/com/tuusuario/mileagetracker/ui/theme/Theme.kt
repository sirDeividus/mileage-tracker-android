package com.tuusuario.mileagetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Theme.kt
 * -----------------------------------------------------------------------
 * Ensambla el ColorScheme (colores) + Typography (tipografía) en un solo
 * "MaterialTheme" que envolverá toda la app. Es el equivalente a definir
 * un ThemeProvider en una app web.
 *
 * Por ahora forzamos siempre el esquema claro (lightColorScheme), ya que
 * el modo oscuro quedó en el roadmap de la v2.
 * -----------------------------------------------------------------------
 */

private val AppColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = TextOnPrimary,
    secondary = AccentOrange,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = DangerRed,
)

@Composable
fun MileageTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
