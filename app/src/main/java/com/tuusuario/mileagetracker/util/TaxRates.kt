package com.tuusuario.mileagetracker.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TaxRates.kt
 * -----------------------------------------------------------------------
 * Tasas oficiales de deducción por milla publicadas por el IRS.
 *
 * IMPORTANTE: el IRS puede cambiar la tasa varias veces al año (como
 * ocurrió en 2026, con un cambio el 1 de julio). Por eso guardamos la
 * tasa como una LISTA de rangos de fecha, no como un solo número fijo.
 *
 * Fuente oficial: irs.gov/tax-professionals/standard-mileage-rates
 *
 * ESTA TASA ES FEDERAL: es exactamente la misma para cualquier usuario,
 * sin importar en qué estado de EE.UU. viva (antes esta app solo
 * mencionaba North Carolina; ver UsStates.kt para la nota específica de
 * cada uno de los 50 estados + DC). Esta app NO sustituye asesoría
 * fiscal profesional.
 * -----------------------------------------------------------------------
 */

data class MileageRateRange(val from: String, val to: String, val rate: Double)

val BUSINESS_MILEAGE_RATES = listOf(
    MileageRateRange("2025-01-01", "2025-12-31", 0.70),
    MileageRateRange("2026-01-01", "2026-06-30", 0.725),
    MileageRateRange("2026-07-01", "2026-12-31", 0.76),
)

private val FALLBACK_RATE = BUSINESS_MILEAGE_RATES.last().rate
private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

/** Devuelve la tasa (USD/milla) vigente para una fecha dada. */
fun getRateForDate(date: Date): Double {
    val iso = isoFormat.format(date)
    val match = BUSINESS_MILEAGE_RATES.find { iso >= it.from && iso <= it.to }
    return match?.rate ?: FALLBACK_RATE
}

/** Resultado del cálculo de deducción de un viaje. */
data class DeductionResult(val miles: Double, val rate: Double, val deduction: Double)

fun calculateDeduction(miles: Double, date: Date): DeductionResult {
    val rate = getRateForDate(date)
    val deduction = Math.round(miles * rate * 100) / 100.0
    return DeductionResult(miles, rate, deduction)
}
