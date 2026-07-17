package com.tuusuario.mileagetracker.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuusuario.mileagetracker.ui.components.PrimaryButton
import com.tuusuario.mileagetracker.ui.components.StatsCard
import com.tuusuario.mileagetracker.ui.theme.*

/**
 * HomeScreen.kt
 * -----------------------------------------------------------------------
 * Pantalla principal. Es "tonta" a propósito: no calcula nada por sí
 * misma, solo lee el HomeUiState del ViewModel y dibuja la interfaz.
 * Toda decisión (iniciar GPS, guardar viaje) se delega al ViewModel.
 *
 * Aquí también manejamos el PERMISO de ubicación, porque en Android
 * pedir permisos es una operación ligada al ciclo de vida de la
 * pantalla (Activity), no a la lógica de negocio pura.
 * -----------------------------------------------------------------------
 */
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Lanzador del diálogo nativo de "Permitir ubicación" de Android
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.startTracking()
        } else {
            Toast.makeText(context, "Se necesita permiso de ubicación para rastrear millas", Toast.LENGTH_LONG).show()
        }
    }

    fun handleStartPressed() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.startTracking()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun handleStopPressed() {
        viewModel.stopTracking { miles ->
            Toast.makeText(
                context,
                "Viaje guardado: ${"%.2f".format(miles)} millas",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundLight) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text("Mileage Tracker", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text("Rastrea tus millas de trabajo en NC", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(20.dp))

            // ---- Tarjeta del contador de millas en vivo ----
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (uiState.isTracking) PrimaryGreen else TextMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "%.2f".format(uiState.currentMiles),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        if (uiState.isTracking) "millas recorridas (en curso)" else "listo para iniciar",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    if (uiState.isTracking) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = uiState.note,
                            onValueChange = { viewModel.updateNote(it) },
                            placeholder = { Text("Motivo del viaje (ej. Visita a cliente)") },
                            modifier = Modifier
                                .fillMaxWidth(0.9f),
                            singleLine = true
                        )
                    }
                }
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, color = DangerRed, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---- Botón principal: cambia de texto/color según isTracking ----
            if (uiState.isTracking) {
                PrimaryButton(title = "Stop Work", isStopVariant = true, onClick = { handleStopPressed() })
            } else {
                PrimaryButton(title = "Start Work", isStopVariant = false, onClick = { handleStartPressed() })
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text("Este mes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatsCard(
                    label = "Millas totales",
                    value = "%.1f".format(uiState.monthMiles),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = "Deducción estimada",
                    value = "$${"%.2f".format(uiState.monthDeduction)}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Esta app calcula una ESTIMACIÓN basada en la tasa estándar de millaje del IRS. " +
                    "No sustituye asesoría fiscal profesional. Consulta a tu contador o a ncdor.gov " +
                    "para tu declaración en North Carolina.",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
