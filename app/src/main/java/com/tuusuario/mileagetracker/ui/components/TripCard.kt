package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.tuusuario.mileagetracker.ui.theme.*
import com.tuusuario.mileagetracker.util.calculateDeduction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TripCard.kt
 * -----------------------------------------------------------------------
 * Representa un solo viaje en la lista del historial. Muestra fecha,
 * nota, millas y la deducción estimada (calculada al vuelo llamando a
 * calculateDeduction, igual que en TripCard.js).
 * -----------------------------------------------------------------------
 */
@Composable
fun TripCard(
    trip: TripEntity,
    onDelete: (TripEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val date = Date(trip.startTimeMillis)
    val result = calculateDeduction(trip.miles, date)
    val dateLabel = remember(trip.id) {
        SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(date)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
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
                Text(dateLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                if (trip.note.isNotBlank()) {
                    Text(trip.note, fontSize = 12.sp, color = TextSecondary)
                }
                Text(
                    "Tasa IRS: \$${"%.3f".format(result.rate)}/milla",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("${"%.1f".format(trip.miles)} mi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "\$${"%.2f".format(result.deduction)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PrimaryGreen
                )
            }

            IconButton(onClick = { onDelete(trip) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = DangerRed)
            }
        }
    }
}

