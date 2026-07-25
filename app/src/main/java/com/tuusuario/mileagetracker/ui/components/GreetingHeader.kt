package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.util.AppStrings
import java.util.Calendar

/**
 * GreetingHeader.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * Muestra un saludo según la hora del día (Buenos días / Buenas tardes /
 * Buenas noches) seguido de una frase motivadora elegida al azar entre
 * varias opciones. La frase se elige UNA vez por composición (con
 * remember), para que no cambie cada vez que la pantalla se redibuja
 * por otro motivo (ej. al actualizar las millas en vivo).
 * -----------------------------------------------------------------------
 */
@Composable
fun GreetingHeader(strings: AppStrings) {
    val colors = LocalAppColors.current

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> strings.greetingMorning
            hour < 19 -> strings.greetingAfternoon
            else -> strings.greetingEvening
        }
    }
    val quote = remember { strings.motivationalQuotes.random() }

    Column {
        androidx.compose.material3.Text(
            greeting,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        androidx.compose.material3.Text(
            quote,
            fontSize = 13.sp,
            color = colors.textMuted
        )
    }
}
