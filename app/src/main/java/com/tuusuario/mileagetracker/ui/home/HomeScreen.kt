package com.tuusuario.mileagetracker.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.ui.components.DonateButton
import com.tuusuario.mileagetracker.ui.components.GreetingHeader
import com.tuusuario.mileagetracker.ui.components.LocationDisclosureDialog
import com.tuusuario.mileagetracker.ui.components.PlatformSelector
import com.tuusuario.mileagetracker.ui.components.PrimaryButton
import com.tuusuario.mileagetracker.ui.components.StatsCard
import com.tuusuario.mileagetracker.ui.components.TipDialog
import com.tuusuario.mileagetracker.ui.theme.*
import com.tuusuario.mileagetracker.util.LocalAppStrings

/**
 * HomeScreen.kt  (ACTUALIZADO v2.2)
 * -----------------------------------------------------------------------
 * Novedades de esta versión:
 *   1. Saludo según la hora del día + frase motivadora (GreetingHeader).
 *   2. Modal de recordatorio ("activa Start Work antes de salir, Stop
 *      Work al llegar a casa") que aparece una vez al día como máximo.
 *   3. Todos los textos ahora salen de LocalAppStrings (español/inglés)
 *      en vez de estar escritos a mano, para que el selector de idioma
 *      de Ajustes funcione en toda la pantalla.
 *   4. Los colores salen de LocalAppColors, para que el tema oscuro/
 *      claro/automático de Ajustes se refleje aquí también.
 * -----------------------------------------------------------------------
 */
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val prefs = remember { UserPreferences(context) }

    // NUEVO: controla si mostramos el diálogo de aviso previo (requerido por Google Play)
    var showDisclosure by remember { mutableStateOf(false) }

    // NUEVO: controla el modal de recordatorio diario ("no pierdas millas")
    var showDailyTip by remember { mutableStateOf(prefs.lastTipShownDate != todayDateString()) }

    // Paso 1 (NUEVO v2.0): permiso de ubicación en SEGUNDO plano.
    // Se declara primero porque foregroundPermissionLauncher lo referencia.
    // Android exige pedirlo en un diálogo SEPARADO del de primer plano.
    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Continuamos igual aunque el usuario lo rechace: la app sigue
        // funcionando con la pantalla encendida, solo pierde precisión
        // con la pantalla apagada. Se lo explicamos con un Toast.
        viewModel.startTracking()
    }

    // Paso 2: permiso de ubicación en PRIMER plano (igual que en v1.0)
    val foregroundPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Reintenta el flujo completo (puede que ahora pida el de background)
            val needsBackgroundPrompt = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            if (needsBackgroundPrompt) {
                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                viewModel.startTracking()
            }
        } else {
            Toast.makeText(context, "Se necesita permiso de ubicación para rastrear millas", Toast.LENGTH_LONG).show()
        }
    }

    fun handleStartPressed() {
        val hasForeground = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasForeground) {
            // Paso 1: sin permiso de primer plano todavía, lo pedimos primero.
            foregroundPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        // Paso 2: ya hay permiso de primer plano. En Android 10+ revisamos
        // si falta el de segundo plano (mejora la precisión con pantalla apagada).
        val needsBackgroundPrompt = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        if (needsBackgroundPrompt) {
            // NUEVO: primero mostramos NUESTRO propio aviso (prominent disclosure);
            // recién cuando el usuario acepta ahí, se abre el diálogo nativo de Android.
            showDisclosure = true
        } else {
            viewModel.startTracking()
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

    // NUEVO: modal de recordatorio diario, se muestra antes que cualquier otro diálogo
    if (showDailyTip) {
        TipDialog(
            strings = strings,
            onDismiss = { dontShowToday ->
                showDailyTip = false
                if (dontShowToday) {
                    prefs.lastTipShownDate = todayDateString()
                }
            }
        )
    }

    // NUEVO: diálogo de aviso previo obligatorio de Google Play
    if (showDisclosure) {
        LocationDisclosureDialog(
            onAccept = {
                showDisclosure = false
                Toast.makeText(
                    context,
                    "Elige 'Permitir todo el tiempo' en el siguiente diálogo para máxima precisión",
                    Toast.LENGTH_LONG
                ).show()
                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            },
            onDecline = {
                showDisclosure = false
                viewModel.startTracking() // sigue funcionando, solo con menos precisión en 2do plano
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // NUEVO: saludo por hora del día + frase motivadora
            GreetingHeader(strings = strings)
            Spacer(modifier = Modifier.height(10.dp))

            Text(strings.appTitle, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = colors.textPrimary)
            Text(strings.homeSubtitle, fontSize = 14.sp, color = colors.textSecondary)

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (uiState.isTracking) PrimaryGreen else colors.textMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "%.2f".format(uiState.currentMiles),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary
                    )
                    Text(
                        if (uiState.isTracking) strings.trackingInProgress else strings.readyToStart,
                        fontSize = 13.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    // Selector de plataforma en vez del campo de texto libre
                    if (uiState.isTracking) {
                        Spacer(modifier = Modifier.height(18.dp))
                        PlatformSelector(
                            selectedId = uiState.selectedPlatformId,
                            onSelect = { viewModel.selectPlatform(it) },
                            customName = uiState.customPlatformName,
                            onCustomNameChange = { viewModel.updateCustomPlatformName(it) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // NUEVO v2.3: campo opcional de peajes. El IRS permite
                        // deducirlos por separado de la deducción por millaje.
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = uiState.tollAmountText,
                            onValueChange = { viewModel.updateTollAmount(it) },
                            label = { Text(strings.tollLabel) },
                            placeholder = { Text(strings.tollHint) },
                            leadingIcon = { Text("$", fontSize = 16.sp, color = colors.textSecondary) },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }
                }
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, color = DangerRed, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.isTracking) {
                PrimaryButton(title = strings.stopWork, isStopVariant = true, onClick = { handleStopPressed() })
            } else {
                PrimaryButton(title = strings.startWork, isStopVariant = false, onClick = { handleStartPressed() })
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(strings.thisMonth, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatsCard(
                    label = strings.totalMiles,
                    value = "%.1f".format(uiState.monthMiles),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    label = strings.estimatedDeduction,
                    value = "$${"%.2f".format(uiState.monthDeduction)}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            DonateButton()

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                strings.disclaimerHome,
                fontSize = 11.sp,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Fecha de hoy en formato "YYYY-MM-DD", usada para no repetir el tip más de una vez al día. */
private fun todayDateString(): String {
    val cal = java.util.Calendar.getInstance()
    return "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.MONTH)}-${cal.get(java.util.Calendar.DAY_OF_MONTH)}"
}
