package com.tuusuario.mileagetracker.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.tuusuario.mileagetracker.ui.theme.*
import com.tuusuario.mileagetracker.util.BUSINESS_MILEAGE_RATES
import com.tuusuario.mileagetracker.util.NcTaxInfo

/**
 * SummaryScreen.kt
 * -----------------------------------------------------------------------
 * Pantalla "Resumen fiscal": el usuario elige un período con chips
 * (Este mes / Trimestre / Este año) y ve la deducción estimada, la
 * tabla de tasas del IRS usadas, y notas sobre North Carolina.
 * -----------------------------------------------------------------------
 */
@Composable
fun SummaryScreen() {
    val viewModel: SummaryViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundLight) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text("Resumen fiscal (IRS)", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text("Estimación de deducción por millaje - North Carolina", fontSize = 13.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(18.dp))

            // ---- Selector de período (chips) ----
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeriodChip("Este mes", uiState.period == Period.MONTH) { viewModel.setPeriod(Period.MONTH) }
                PeriodChip("Trimestre", uiState.period == Period.QUARTER) { viewModel.setPeriod(Period.QUARTER) }
                PeriodChip("Este año", uiState.period == Period.YEAR) { viewModel.setPeriod(Period.YEAR) }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---- Tarjeta grande de deducción total ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryGreen, RoundedCornerShape(20.dp))
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$${"%.2f".format(uiState.totalDeduction)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = TextOnPrimary
                    )
                    Text("Deducción estimada", fontSize = 13.sp, color = TextOnPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStat("%.1f".format(uiState.totalMiles), "Millas totales", Modifier.weight(1f))
                SmallStat("${uiState.tripCount}", "Viajes", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text("Tasas del IRS usadas", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            BUSINESS_MILEAGE_RATES.forEach { range ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite, RoundedCornerShape(10.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${range.from} → ${range.to}", fontSize = 12.sp, color = TextSecondary)
                    Text("\$${"%.3f".format(range.rate)} / milla", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Sobre North Carolina", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(NcTaxInfo.NOTES, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Este resumen es solo una guía informativa. No constituye asesoría legal ni fiscal. " +
                    "Verifica siempre con un profesional certificado (CPA) o con el IRS (irs.gov) y " +
                    "NCDOR (ncdor.gov) antes de presentar tu declaración.",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
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
    Column(
        modifier = modifier
            .background(SurfaceWhite, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}
