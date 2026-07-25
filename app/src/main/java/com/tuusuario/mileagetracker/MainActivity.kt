package com.tuusuario.mileagetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tuusuario.mileagetracker.data.local.AppLanguage
import com.tuusuario.mileagetracker.data.local.ThemeMode
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.ui.navigation.AppNavigation
import com.tuusuario.mileagetracker.ui.theme.MileageTrackerTheme
import com.tuusuario.mileagetracker.util.LocalAppStrings
import com.tuusuario.mileagetracker.util.stringsFor

/**
 * MainActivity.kt
 * -----------------------------------------------------------------------
 * Punto de entrada de la app. Ahora, además de aplicar el tema, lee las
 * preferencias guardadas (idioma y tema) y las distribuye a toda la app
 * mediante CompositionLocalProvider — cualquier pantalla puede leer
 * LocalAppStrings.current para obtener el texto en el idioma correcto,
 * sin tener que pasarlo manualmente de pantalla en pantalla.
 * -----------------------------------------------------------------------
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = UserPreferences(this)

        setContent {
            // remember + mutableStateOf: si el usuario cambia el idioma o el
            // tema en Ajustes, estas variables se actualizan y TODA la app
            // se redibuja automáticamente con el nuevo valor.
            var language by remember { mutableStateOf(prefs.language) }
            var themeMode by remember { mutableStateOf(prefs.themeMode) }

            MileageTrackerTheme(themeMode = themeMode) {
                CompositionLocalProvider(LocalAppStrings provides stringsFor(language)) {
                    AppNavigation(
                        currentLanguage = language,
                        currentThemeMode = themeMode,
                        onLanguageChange = { newLang ->
                            prefs.language = newLang
                            language = newLang
                        },
                        onThemeModeChange = { newTheme ->
                            prefs.themeMode = newTheme
                            themeMode = newTheme
                        }
                    )
                }
            }
        }
    }
}
