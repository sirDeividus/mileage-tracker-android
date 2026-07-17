package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.ui.theme.*

/**
 * StatsCard.kt
 * -----------------------------------------------------------------------
 * Tarjeta pequeña que muestra un número destacado con una etiqueta debajo
 * (ej. "124.5 mi" / "Millas este mes"). Equivalente a StatsCard.js.
 * -----------------------------------------------------------------------
 */
@Composable
fun StatsCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = PrimaryGreen,
) {
    Card(
        modifier = modifier.border(1.dp, BorderColor, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
