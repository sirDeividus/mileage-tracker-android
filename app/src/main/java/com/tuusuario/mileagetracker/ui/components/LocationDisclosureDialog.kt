package com.tuusuario.mileagetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.ui.theme.TextPrimary
import com.tuusuario.mileagetracker.ui.theme.TextSecondary

/**
 * LocationDisclosureDialog.kt  (NUEVO — requerido por Google Play)
 * -----------------------------------------------------------------------
 * Google Play EXIGE que, antes de pedir el permiso de ubicación en
 * segundo plano (ACCESS_BACKGROUND_LOCATION), la app muestre su PROPIA
 * pantalla explicando con palabras simples por qué lo necesita — esto se
 * llama "prominent disclosure". No puede ir solo en la descripción de la
 * tienda ni en la política de privacidad: tiene que aparecer DENTRO de
 * la app, justo antes del diálogo nativo de Android.
 *
 * Sin esta pantalla, Google RECHAZA la publicación de apps que declaran
 * ACCESS_BACKGROUND_LOCATION en el manifest.
 * -----------------------------------------------------------------------
 */
@Composable
fun LocationDisclosureDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDecline,
        icon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGreen) },
        title = {
            Text(
                "Mileage Tracker usa tu ubicación en segundo plano",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    "Para calcular correctamente tus millas de trabajo, esta app necesita seguir " +
                        "midiendo tu ubicación GPS incluso cuando minimizas la app o apagas la " +
                        "pantalla mientras un viaje está en curso.",
                    fontSize = 13.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Sin este permiso, las millas registradas pueden ser inexactas si dejas el " +
                        "teléfono guardado durante el viaje. Tu ubicación NUNCA se comparte con " +
                        "terceros ni se usa para publicidad — solo se guarda localmente en tu " +
                        "teléfono para calcular tu deducción de impuestos.",
                    fontSize = 13.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Start
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onAccept) { Text("Continuar", color = PrimaryGreen, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDecline) { Text("Ahora no", color = TextSecondary) }
        }
    )
}
