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
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.ui.theme.DangerRed
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.util.AppStrings
import com.tuusuario.mileagetracker.util.DELIVERY_PLATFORMS
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * EditTripDialog.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * Un solo diálogo que sirve para DOS casos, según si "existingTrip" es
 * null o no:
 *   1. Editar un viaje ya guardado (el usuario tocó "Editar" en el
 *      Historial) — por si el GPS midió mal, o quiere corregir la
 *      plataforma o los peajes.
 *   2. Agregar un viaje manual desde cero (el botón "+" del Historial)
 *      — por si el usuario se le olvidó presionar "Start Work" y quiere
 *      registrar esas millas de todos modos para no perder la deducción.
 * -----------------------------------------------------------------------
 */
@Composable
fun EditTripDialog(
    strings: AppStrings,
    existingTrip: TripEntity?,
    onDismiss: () -> Unit,
    onSave: (TripEntity) -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val knownPlatformIds = remember { DELIVERY_PLATFORMS.map { it.id }.toSet() }

    var selectedPlatformId by remember {
        mutableStateOf(existingTrip?.platform?.takeIf { it in knownPlatformIds } ?: "")
    }
    var customPlatformName by remember {
        mutableStateOf(existingTrip?.platform?.takeIf { it.isNotBlank() && it !in knownPlatformIds } ?: "")
    }
    var dateText by remember {
        mutableStateOf(dateFormat.format(Date(existingTrip?.startTimeMillis ?: System.currentTimeMillis())))
    }
    var milesText by remember { mutableStateOf(existingTrip?.miles?.let { "%.2f".format(it) } ?: "") }
    var tollText by remember {
        mutableStateOf(existingTrip?.tollAmount?.takeIf { it > 0.0 }?.let { "%.2f".format(it) } ?: "")
    }
    var noteText by remember { mutableStateOf(existingTrip?.note ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existingTrip != null) strings.editTripTitle else strings.addTripTitle,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                if (existingTrip == null) {
                    Text(strings.manualTripExplanation, fontSize = 12.5.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text(strings.fieldDate) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = milesText,
                    onValueChange = { milesText = it },
                    label = { Text(strings.fieldMiles) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                PlatformSelector(
                    selectedId = selectedPlatformId,
                    onSelect = { selectedPlatformId = it.id },
                    customName = customPlatformName,
                    onCustomNameChange = { customPlatformName = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tollText,
                    onValueChange = { tollText = it },
                    label = { Text(strings.tollLabel) },
                    placeholder = { Text(strings.tollHint) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(strings.fieldNote) },
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(it, color = DangerRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val miles = milesText.replace(",", ".").toDoubleOrNull()
                val parsedDate = try {
                    dateFormat.parse(dateText)
                } catch (e: Exception) {
                    null
                }

                if (miles == null || miles <= 0.0 || parsedDate == null) {
                    errorMessage = strings.invalidMilesError
                    return@TextButton
                }

                val tolls = tollText.replace(",", ".").toDoubleOrNull() ?: 0.0
                val platformValue = if (selectedPlatformId == "other") customPlatformName.trim() else selectedPlatformId
                val startMillis = parsedDate.time

                val base = existingTrip ?: TripEntity(
                    startTimeMillis = startMillis,
                    endTimeMillis = startMillis,
                    miles = miles,
                    note = noteText,
                    routeJson = "[]",
                    platform = platformValue,
                    tollAmount = tolls,
                )
                val trip = base.copy(
                    startTimeMillis = startMillis,
                    miles = miles,
                    note = noteText,
                    platform = platformValue,
                    tollAmount = tolls,
                )
                onSave(trip)
            }) {
                Text(strings.save, color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
