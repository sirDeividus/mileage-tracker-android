package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.ui.theme.AccentOrange
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.ui.theme.TextOnPrimary

/**
 * PrimaryButton.kt
 * -----------------------------------------------------------------------
 * Botón grande y reutilizable — el equivalente exacto de PrimaryButton.js
 * en la versión React Native. Se usa para "Start Work" / "Stop Work".
 *
 * En Compose, un componente reutilizable es simplemente una función
 * marcada con @Composable que recibe parámetros (como "props" en React).
 * -----------------------------------------------------------------------
 */
@Composable
fun PrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isStopVariant: Boolean = false,
    enabled: Boolean = true,
) {
    val backgroundColor = if (isStopVariant) AccentOrange else PrimaryGreen

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = TextOnPrimary,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        )
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}
