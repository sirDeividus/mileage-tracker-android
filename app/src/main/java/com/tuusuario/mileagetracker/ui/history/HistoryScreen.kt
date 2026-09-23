package com.tuusuario.mileagetracker.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.ui.components.EditTripDialog
import com.tuusuario.mileagetracker.ui.components.TripCard
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.util.LocalAppStrings

/**
 * HistoryScreen.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Muestra la lista completa de viajes usando LazyColumn. Ahora usa
 * LocalAppStrings (idioma elegido en Ajustes) y LocalAppColors (tema
 * claro/oscuro/automático) en vez de textos y colores fijos.
 * -----------------------------------------------------------------------
 */
@Composable
fun HistoryScreen() {
    val viewModel: HistoryViewModel = viewModel()
    val trips by viewModel.trips.collectAsState()
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // NUEVO: controla el diálogo de "Editar viaje" / "Agregar viaje manual".
    // null = cerrado. Un TripEntity con id == 0 = modo "agregar nuevo".
    var editingTrip by remember { mutableStateOf<TripEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTrip = null
                    showEditor = true
                },
                containerColor = PrimaryGreen,
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.addTripFab)
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues), color = colors.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 8.dp)) {
                    Text(strings.historyTitle, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colors.textPrimary)
                    Text("${trips.size} ${strings.tripsRegistered}", fontSize = 13.sp, color = colors.textSecondary)
                }

                if (trips.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            strings.noTripsYet,
                            fontSize = 13.sp,
                            color = colors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(trips, key = { it.id }) { trip ->
                            TripCard(
                                trip = trip,
                                onDelete = { viewModel.deleteTrip(it) },
                                onEdit = {
                                    editingTrip = it
                                    showEditor = true
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        EditTripDialog(
            strings = strings,
            existingTrip = editingTrip,
            onDismiss = { showEditor = false },
            onSave = { trip ->
                viewModel.saveTrip(trip)
                showEditor = false
            },
        )
    }
}
