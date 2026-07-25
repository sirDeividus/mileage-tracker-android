package com.tuusuario.mileagetracker.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuusuario.mileagetracker.ui.components.DonateButton
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.ui.theme.TextOnPrimary
import com.tuusuario.mileagetracker.util.BUSINESS_MILEAGE_RATES
import com.tuusuario.mileagetracker.util.LocalAppStrings
import com.tuusuario.mileagetracker.util.stateTaxNotes

/**
 * SummaryScreen.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Pantalla "Resumen fiscal". CAMBIO IMPORTANTE: ya no muestra una nota
 * fija de "North Carolina" — ahora usa el estado que el usuario eligió
 * en Ajustes (o invita a elegirlo si aún no lo hizo), válido para
 * cualquiera de los 50 estados de EE.UU. También usa LocalAppStrings
 * para mostrar el texto en el idioma elegido, y LocalAppColors para
 * respetar el tema claro/oscuro.
 * -----------------------------------------------------------------------
 */
@Composable
fun SummaryScreen() {
    val viewModel: SummaryViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // Por si el usuario cambió su estado en Ajustes y vuelve a esta pantalla
    LaunchedEffect(Unit) { viewModel.refreshSelectedState() }

    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(strings.summaryTitle, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colors.textPrimary)
            Text(strings.summarySubtitle, fontSize = 13.sp, color = colors.textSecondary)

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeriodChip(strings.periodMonth, uiState.period == Period.MONTH) { viewModel.setPeriod(Period.MONTH) }
                PeriodChip(strings.periodQuarter, uiState.period == Period.QUARTER) { viewModel.setPeriod(Period.QUARTER) }
                PeriodChip(strings.periodYear, uiState.period == Period.YEAR) { viewModel.setPeriod(Period.YEAR) }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryGreen, RoundedCornerShape(20.dp))
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        // NUEVO v2.3: la tarjeta principal ahora suma millaje + peajes,
                        // que es la deducción total real que el usuario puede reclamar.
                        "$${"%.2f".format(uiState.combinedDeduction)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = TextOnPrimary
                    )
                    Text(strings.combinedDeductionLabel, fontSize = 13.sp, color = TextOnPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // NUEVO v2.3: desglose de millaje vs. peajes, y explicación de por qué se suman aparte
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStat("$${"%.2f".format(uiState.totalDeduction)}", strings.estimatedDeductionLabel, Modifier.weight(1f))
                SmallStat("$${"%.2f".format(uiState.totalTolls)}", strings.totalTollsLabel, Modifier.weight(1f))
            }
            if (uiState.totalTolls > 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(strings.tollExplanation, fontSize = 11.sp, color = colors.textMuted, lineHeight = 15.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStat("%.1f".format(uiState.totalMiles), strings.totalMilesLabel, Modifier.weight(1f))
                SmallStat("${uiState.tripCount}", strings.tripsLabel, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(strings.irsRatesUsed, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            BUSINESS_MILEAGE_RATES.forEach { range ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, RoundedCornerShape(10.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${range.from} → ${range.to}", fontSize = 12.sp, color = colors.textSecondary)
                    Text("\$${"%.3f".format(range.rate)} / milla", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(strings.aboutYourState, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            val state = uiState.selectedState
            if (state != null) {
                Text(stateTaxNotes(state), fontSize = 13.sp, color = colors.textSecondary, lineHeight = 20.sp)
            } else {
                // El usuario aún no eligió su estado — lo invitamos a hacerlo en Ajustes
                Text(
                    "${strings.chooseYourState} → ${strings.settingsTitle}",
                    fontSize = 13.sp,
                    color = colors.textMuted,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                strings.summaryDisclaimer,
                fontSize = 11.sp,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            DonateButton()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PeriodChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryGreen,
            selectedLabelColor = TextOnPrimary
        )
    )
}

@Composable
private fun SmallStat(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .background(colors.surface, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = colors.textPrimary)
        Text(label, fontSize = 12.sp, color = colors.textSecondary)
    }
}
