package com.tuusuario.mileagetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.tuusuario.mileagetracker.data.local.ThemeMode

/**
 * Theme.kt  (ACTUALIZADO — agrega soporte de tema oscuro)
 * -----------------------------------------------------------------------
 * Ensambla el ColorScheme (colores) + Typography (tipografía) en un solo
 * "MaterialTheme" que envolverá toda la app, y además distribuye
 * LocalAppColors (ver AppColors.kt) para que cada pantalla pueda leer
 * colores que sí reaccionan al modo claro/oscuro.
 *
 * NUEVO: ahora soporta 3 modos (ver ThemeMode en UserPreferences.kt):
 *   - LIGHT: siempre claro
 *   - DARK: siempre oscuro
 *   - AUTO: sigue el tema del sistema operativo del teléfono
 * -----------------------------------------------------------------------
 */

private val LightScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = TextOnPrimary,
    secondary = AccentOrange,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = DangerRed,
)

private val DarkScheme = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = Color(0xFF0B1F0C),
    secondary = AccentOrange,
    background = Color(0xFF121512),
    surface = Color(0xFF1C201C),
    onBackground = Color(0xFFE7ECE8),
    onSurface = Color(0xFFE7ECE8),
    error = Color(0xFFFF6B6B),
)

@Composable
fun MileageTrackerTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(
        LocalAppColors provides if (useDarkTheme) DarkAppColors else LightAppColors
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) DarkScheme else LightScheme,
            typography = AppTypography,
            content = content
        )
    }
}
