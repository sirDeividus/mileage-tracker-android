package com.tuusuario.mileagetracker.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * UserPreferences.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * Guarda las preferencias simples del usuario: idioma, tema (claro/
 * oscuro/automático), estado de EE.UU. elegido, y si ya vio el
 * recordatorio de "activa Start Work antes de salir". Usamos
 * SharedPreferences (más simple que Room) porque son solo unos pocos
 * valores sueltos, no una tabla de datos.
 * -----------------------------------------------------------------------
 */
enum class ThemeMode { LIGHT, DARK, AUTO }
enum class AppLanguage(val code: String) { SPANISH("es"), ENGLISH("en") }

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("mileage_tracker_prefs", Context.MODE_PRIVATE)

    var language: AppLanguage
        get() = if (prefs.getString(KEY_LANGUAGE, "es") == "en") AppLanguage.ENGLISH else AppLanguage.SPANISH
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value.code).apply()

    var themeMode: ThemeMode
        get() = when (prefs.getString(KEY_THEME, "auto")) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.AUTO
        }
        set(value) = prefs.edit().putString(KEY_THEME, value.name.lowercase()).apply()

    /** Código de 2 letras del estado elegido (ej. "NC", "TX"). Vacío si aún no eligió. */
    var stateCode: String
        get() = prefs.getString(KEY_STATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_STATE, value).apply()

    /** Si ya se le mostró el recordatorio hoy (para no ser repetitivo). */
    var lastTipShownDate: String
        get() = prefs.getString(KEY_TIP_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TIP_DATE, value).apply()

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_STATE = "state_code"
        private const val KEY_TIP_DATE = "last_tip_shown_date"
    }
}
