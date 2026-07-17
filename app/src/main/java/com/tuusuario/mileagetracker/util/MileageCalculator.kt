package com.tuusuario.mileagetracker.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * MileageCalculator.kt
 * -----------------------------------------------------------------------
 * Funciones puras de Kotlin (sin dependencias de Android) para calcular
 * distancias GPS y deducciones de impuestos. Al ser funciones puras
 * (mismo input -> mismo output, sin efectos secundarios), son muy fáciles
 * de probar con unit tests.
 * -----------------------------------------------------------------------
 */

private const val EARTH_RADIUS_MILES = 3958.8

/** Un punto GPS simple: latitud, longitud y momento en que se registró. */
data class GpsPoint(val latitude: Double, val longitude: Double, val timestampMillis: Long)

/**
 * Calcula la distancia en millas entre dos puntos GPS usando la fórmula
 * de Haversine (distancia sobre la superficie de una esfera).
 */
fun haversineDistanceMiles(a: GpsPoint, b: GpsPoint): Double {
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val lat1 = Math.toRadians(a.latitude)
    val lat2 = Math.toRadians(b.latitude)

    val h = sin(dLat / 2).let { it * it } +
            sin(dLon / 2).let { it * it } * cos(lat1) * cos(lat2)

    val c = 2 * atan2(sqrt(h), sqrt(1 - h))
    return EARTH_RADIUS_MILES * c
}

/**
 * Suma la distancia total recorrida a lo largo de una lista ordenada de
 * puntos GPS (recorre la lista y suma la distancia entre cada par
 * consecutivo).
 */
fun calculateTotalDistance(points: List<GpsPoint>): Double {
    if (points.size < 2) return 0.0
    var total = 0.0
    for (i in 1 until points.size) {
        total += haversineDistanceMiles(points[i - 1], points[i])
    }
    return Math.round(total * 100) / 100.0
}

/**
 * Filtra puntos GPS con "saltos" imposibles (errores de señal), igual
 * que hicimos en la versión React Native: si la velocidad implícita
 * entre dos puntos supera 150 mph, se descarta ese punto.
 */
fun filterGpsNoise(points: List<GpsPoint>): List<GpsPoint> {
    if (points.size < 2) return points
    val maxReasonableMph = 150.0
    val cleaned = mutableListOf(points.first())

    for (i in 1 until points.size) {
        val prev = cleaned.last()
        val curr = points[i]
        val distance = haversineDistanceMiles(prev, curr)
        val hours = (curr.timestampMillis - prev.timestampMillis) / 3_600_000.0
        val speed = if (hours > 0) distance / hours else 0.0

        if (speed <= maxReasonableMph) {
            cleaned.add(curr)
        }
    }
    return cleaned
}
