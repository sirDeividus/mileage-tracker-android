package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.ui.theme.TextPrimary
import com.tuusuario.mileagetracker.ui.theme.TextSecondary
import com.tuusuario.mileagetracker.util.AppStrings

/**
 * TipDialog.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * Modal que recuerda al usuario dos hábitos clave para no perder millas
 * deducibles:
 *   1. Activar "Start Work" apenas va a salir a trabajar (si empieza
 *      tarde, esas millas ya no se cuentan).
 *   2. Presionar "Stop Work" al llegar a casa (si lo olvida, sigue
 *      sumando millas personales como si fueran de trabajo).
 *
 * Incluye un checkbox "No mostrar de nuevo hoy" para no ser repetitivo
 * — HomeScreen decide, usando UserPreferences, si corresponde mostrarlo.
 * -----------------------------------------------------------------------
 */
@Composable
fun TipDialog(
    strings: AppStrings,
    onDismiss: (dontShowToday: Boolean) -> Unit,
) {
    var dontShowAgain by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss(dontShowAgain) },
        title = {
            Text(strings.tipTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        },
        text = {
            Column {
                Text(strings.tipBody, fontSize = 13.5.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { dontShowAgain = !dontShowAgain }
                ) {
                    Checkbox(
                        checked = dontShowAgain,
                        onCheckedChange = { dontShowAgain = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen)
                    )
                    Text(strings.tipDontShowAgain, fontSize = 12.5.sp, color = TextSecondary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss(dontShowAgain) }) {
                Text(strings.tipGotIt, color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}
