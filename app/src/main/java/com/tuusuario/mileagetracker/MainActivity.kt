package com.tuusuario.mileagetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuusuario.mileagetracker.ui.navigation.AppNavigation
import com.tuusuario.mileagetracker.ui.theme.MileageTrackerTheme

/**
 * MainActivity.kt
 * -----------------------------------------------------------------------
 * Punto de entrada de la app — el equivalente de App.js en la versión
 * React Native. Se mantiene deliberadamente simple: solo aplica el tema
 * y monta la navegación. Toda la lógica real vive en los paquetes
 * data/, domain/, location/ y ui/.
 *
 * setContent { } es la función que "conecta" el mundo clásico de
 * Android (Activity) con Jetpack Compose.
 * -----------------------------------------------------------------------
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MileageTrackerTheme {
                AppNavigation()
            }
        }
    }
}
