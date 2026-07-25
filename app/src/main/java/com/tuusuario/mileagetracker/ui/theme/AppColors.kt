package com.tuusuario.mileagetracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * AppColors.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * Antes, cada pantalla usaba directamente constantes fijas como
 * "BackgroundLight" o "TextPrimary" — por eso el tema oscuro no cambiaba
 * nada visualmente. Esta clase agrupa los colores que SÍ deben cambiar
 * entre modo claro y oscuro, y LocalAppColors los distribuye a toda la
 * app según el ThemeMode activo (ver Theme.kt).
 * -----------------------------------------------------------------------
 */
data class AppColorSet(
    val background: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
)

val LightAppColors = AppColorSet(
    background = BackgroundLight,
    surface = SurfaceWhite,
    surfaceAlt = SurfaceAlt,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    border = BorderColor,
)

val DarkAppColors = AppColorSet(
    background = Color(0xFF121512),
    surface = Color(0xFF1C201C),
    surfaceAlt = Color(0xFF262B26),
    textPrimary = Color(0xFFE7ECE8),
    textSecondary = Color(0xFFAEB8B1),
    textMuted = Color(0xFF7C877E),
    border = Color(0xFF33382F),
)

val LocalAppColors = compositionLocalOf { LightAppColors }
