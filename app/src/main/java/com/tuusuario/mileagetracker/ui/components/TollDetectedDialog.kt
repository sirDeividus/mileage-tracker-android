package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.util.AppStrings
import com.tuusuario.mileagetracker.util.TollDetectionResult

/**
 * TollDetectedDialog.kt  (NUEVO v2.5)
 * -----------------------------------------------------------------------
 * Aparece cuando TollDetector.kt encuentra que la ruta del viaje que
 * acabas de terminar pasó cerca de una caseta de peaje conocida (ver
 * HomeViewModel). No inventamos un monto — le pedimos al usuario que lo
 * confirme, porque el precio real depende de cosas que el GPS no puede
 * saber (tipo de vehículo, hora, descuentos como E-ZPass/SunPass).
 * -----------------------------------------------------------------------
 */
@Composable
fun TollDetectedDialog(
    strings: AppStrings,
    detection: TollDetectionResult,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.tollDetectedTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(strings.tollDetectedBody)
                if (detection.name.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(detection.name, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(strings.tollLabel) },
                    placeholder = { Text(strings.tollHint) },
                    leadingIcon = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.replace(",", ".").toDoubleOrNull()
                if (amount != null && amount > 0.0) {
                    onConfirm(amount)
                } else {
                    onDismiss()
                }
            }) {
                Text(strings.save, color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.tollDetectedIgnore) }
        }
    )
}
