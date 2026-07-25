package com.tuusuario.mileagetracker.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.util.LocalAppStrings

/**
 * DonateButton.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Botón que abre el navegador directo a tu link de donación de PayPal.
 * El texto ahora sale de LocalAppStrings (cambia con el idioma elegido
 * en Ajustes).
 *
 * IMPORTANTE — debes reemplazar la constante PAYPAL_DONATE_URL con tu
 * propio link real antes de publicar la app:
 *
 *   1. Ve a paypal.com/donate/buttons (o paypal.me) y crea tu link.
 *   2. La forma más simple es un link de PayPal.me:
 *        https://paypal.me/TUUSUARIO
 *   3. Pega esa URL abajo, reemplazando el valor de ejemplo.
 * -----------------------------------------------------------------------
 */
private const val PAYPAL_DONATE_URL = "https://paypal.me/TUUSUARIO" // <-- reemplaza esto

@Composable
fun DonateButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current

    OutlinedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PAYPAL_DONATE_URL))
            context.startActivity(intent)
        },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = androidx.compose.ui.graphics.Color(0xFF0070BA) // azul de PayPal
        )
    ) {
        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(strings.donateButton, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
