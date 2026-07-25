package com.tuusuario.mileagetracker.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * DeliveryPlatforms.kt  (NUEVO en v2.0)
 * -----------------------------------------------------------------------
 * Lista de plataformas de trabajo (delivery / rideshare) que el usuario
 * puede elegir al finalizar un viaje, en vez de escribir el motivo a mano.
 *
 * NOTA IMPORTANTE sobre los logos: no usamos los logos oficiales de
 * DoorDash, Uber, Amazon, etc. porque son marcas registradas con
 * derechos de autor — reproducirlos sin licencia no es legal, ni
 * siquiera para un proyecto personal público en GitHub. En su lugar,
 * cada plataforma tiene un color distintivo (inspirado en su marca) y
 * un ÍCONO GENÉRICO (auto, entrega, carrito), que es perfectamente legal
 * de usar y sigue dando una identidad visual clara e inmediata.
 * -----------------------------------------------------------------------
 */
data class DeliveryPlatform(
    val id: String,
    val displayName: String,
    val color: Color,
    val icon: ImageVector,
    val isCustom: Boolean = false,
)

val DELIVERY_PLATFORMS = listOf(
    DeliveryPlatform("doordash", "DoorDash", Color(0xFFFF3008), Icons.Default.DeliveryDining),
    DeliveryPlatform("amazon_flex", "Amazon Flex", Color(0xFFFF9900), Icons.Default.Inventory2),
    DeliveryPlatform("spark_driver", "Spark Driver", Color(0xFF0071CE), Icons.Default.LocalShipping),
    DeliveryPlatform("uber_eats", "Uber Eats", Color(0xFF06C167), Icons.Default.RestaurantMenu),
    DeliveryPlatform("uber", "Uber", Color(0xFF000000), Icons.Default.DirectionsCar),
    DeliveryPlatform("veho", "Veho", Color(0xFF6C4CE0), Icons.Default.LocalShipping),
    DeliveryPlatform("instacart", "Instacart", Color(0xFF43B02A), Icons.Default.ShoppingCart),
    DeliveryPlatform("roadie", "Roadie", Color(0xFF1E88E5), Icons.Default.LocalShipping),
    DeliveryPlatform("grubhub", "Grubhub", Color(0xFFF63440), Icons.Default.RestaurantMenu),
    DeliveryPlatform("other", "Otra", Color(0xFF78909C), Icons.Default.MoreHoriz, isCustom = true),
)

/** Busca una plataforma por su id guardado en la base de datos. Si no existe, devuelve "Otra". */
fun findPlatformById(id: String): DeliveryPlatform =
    DELIVERY_PLATFORMS.find { it.id == id } ?: DELIVERY_PLATFORMS.last()
