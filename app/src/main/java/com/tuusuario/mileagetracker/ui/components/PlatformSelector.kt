package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.ui.theme.AppColorSet
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.util.DELIVERY_PLATFORMS
import com.tuusuario.mileagetracker.util.DeliveryPlatform
import com.tuusuario.mileagetracker.util.LocalAppStrings

/**
 * PlatformSelector.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Fila horizontal deslizable de "chips" con el nombre de cada plataforma
 * de trabajo. Ahora usa LocalAppStrings (idioma) y LocalAppColors (tema
 * claro/oscuro) en vez de textos y colores fijos.
 * -----------------------------------------------------------------------
 */
@Composable
fun PlatformSelector(
    selectedId: String,
    onSelect: (DeliveryPlatform) -> Unit,
    customName: String,
    onCustomNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    Column(modifier = modifier) {
        Text(
            strings.platformQuestion,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(DELIVERY_PLATFORMS, key = { it.id }) { platform ->
                PlatformChip(
                    platform = platform,
                    selected = platform.id == selectedId,
                    onClick = { onSelect(platform) },
                    colors = colors,
                )
            }
        }

        val selectedPlatform = DELIVERY_PLATFORMS.find { it.id == selectedId }
        if (selectedPlatform?.isCustom == true) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = customName,
                onValueChange = onCustomNameChange,
                placeholder = { Text(strings.customPlatformPlaceholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PlatformChip(
    platform: DeliveryPlatform,
    selected: Boolean,
    onClick: () -> Unit,
    colors: AppColorSet,
) {
    val backgroundColor = if (selected) platform.color else colors.surface
    val contentColor = if (selected) Color.White else colors.textPrimary
    val borderColor = if (selected) platform.color else colors.border

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Icon(platform.icon, contentDescription = platform.displayName, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(platform.displayName, color = contentColor, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
    }
}
