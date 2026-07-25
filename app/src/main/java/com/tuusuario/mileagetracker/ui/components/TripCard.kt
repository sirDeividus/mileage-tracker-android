package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.ui.theme.DangerRed
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.util.DELIVERY_PLATFORMS
import com.tuusuario.mileagetracker.util.LocalAppStrings
import com.tuusuario.mileagetracker.util.calculateDeduction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TripCard.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Muestra la PLATAFORMA del viaje (DoorDash, Uber, etc.) con su color
 * distintivo. Ahora usa LocalAppStrings (idioma) y LocalAppColors (tema
 * claro/oscuro) en vez de textos y colores fijos.
 * -----------------------------------------------------------------------
 */
@Composable
fun TripCard(
    trip: TripEntity,
    onDelete: (TripEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val date = Date(trip.startTimeMillis)
    val result = calculateDeduction(trip.miles, date)
    val dateLabel = remember(trip.id) {
        SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(date)
    }

    // Busca la plataforma conocida por id; si el usuario escribió una
    // personalizada ("Otra"), platform ya contiene ese texto libre.
    val knownPlatform = DELIVERY_PLATFORMS.find { it.id == trip.platform }
    val platformLabel = when {
        knownPlatform != null -> knownPlatform.displayName
        trip.platform.isNotBlank() -> trip.platform
        else -> strings.noPlatform
    }
    val platformColor = knownPlatform?.color ?: colors.textMuted

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.border, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(dateLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)

                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(platformColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(platformLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = platformColor)
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${strings.irsRateLabel}: \$${"%.3f".format(result.rate)}/mi",
                    fontSize = 11.sp,
                    color = colors.textMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("${"%.1f".format(trip.miles)} mi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)
                Text(
                    "\$${"%.2f".format(result.deduction)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PrimaryGreen
                )
            }

            IconButton(onClick = { onDelete(trip) }) {
                Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = DangerRed)
            }
        }
    }
}
