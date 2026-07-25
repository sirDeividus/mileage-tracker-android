package com.tuusuario.mileagetracker.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuusuario.mileagetracker.ui.components.TripCard
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
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

    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
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
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trips, key = { it.id }) { trip ->
                        TripCard(trip = trip, onDelete = { viewModel.deleteTrip(it) })
                    }
                }
            }
        }
    }
}
