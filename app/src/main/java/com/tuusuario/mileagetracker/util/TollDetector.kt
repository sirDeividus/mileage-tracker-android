package com.tuusuario.mileagetracker.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * TollDetector.kt  (NUEVO v2.5)
 * -----------------------------------------------------------------------
 * Detecta si la ruta de un viaje pasó cerca de una caseta de peaje
 * conocida, usando OpenStreetMap (Overpass API) — es gratuita, no
 * necesita API key ni cuenta.
 *
 * LIMITACIÓN IMPORTANTE: esto NO da el monto en dólares. El precio real
 * de un peaje varía según el tipo de vehículo, la hora del día, y si
 * tienes un transpondedor con descuento (E-ZPass, SunPass, etc.) — ningún
 * servicio gratuito lo sabe con certeza. Por eso esta función solo
 * responde "sí, probablemente pasaste por un peaje aquí", y es la propia
 * app (ver HomeViewModel/TollDetectedDialog) la que le pide al usuario
 * que confirme cuánto pagó, en vez de inventar una cifra.
 *
 * Es "mejor esfuerzo": si no hay internet o el servidor no responde a
 * tiempo, devuelve null sin romper nada — el viaje ya se guardó de
 * todas formas antes de intentar esto.
 * -----------------------------------------------------------------------
 */
data class TollDetectionResult(val name: String)

private const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"

// Qué tan cerca (en metros) tiene que pasar la ruta de una caseta de
// peaje para contarla como "cruzada". Ni tan chico que se escape por el
// ruido normal del GPS, ni tan grande que marque peajes de una vía
// paralela que en realidad no tomaste.
private const val PROXIMITY_METERS = 120.0
private const val METERS_PER_MILE = 1609.34

// Margen alrededor de la ruta para el "bounding box" que se le manda a
// Overpass — unos 0.01 grados son ~1 km, suficiente para no perderse
// casetas justo en el borde del recorrido.
private const val BBOX_PADDING_DEGREES = 0.01

suspend fun detectTollAlongRoute(route: List<GpsPoint>): TollDetectionResult? = withContext(Dispatchers.IO) {
    if (route.size < 2) return@withContext null

    val minLat = route.minOf { it.latitude } - BBOX_PADDING_DEGREES
    val maxLat = route.maxOf { it.latitude } + BBOX_PADDING_DEGREES
    val minLon = route.minOf { it.longitude } - BBOX_PADDING_DEGREES
    val maxLon = route.maxOf { it.longitude } + BBOX_PADDING_DEGREES

    val query = """
        [out:json][timeout:15];
        (
          node["barrier"="toll_booth"]($minLat,$minLon,$maxLat,$maxLon);
          way["toll"="yes"]($minLat,$minLon,$maxLat,$maxLon);
        );
        out center;
    """.trimIndent()

    val response = try {
        postOverpassQuery(query)
    } catch (e: Exception) {
        null
    } ?: return@withContext null

    try {
        val elements = JSONObject(response).getJSONArray("elements")
        for (i in 0 until elements.length()) {
            val element = elements.getJSONObject(i)
            val lat: Double
            val lon: Double
            when {
                element.has("lat") && element.has("lon") -> {
                    lat = element.getDouble("lat")
                    lon = element.getDouble("lon")
                }
                element.has("center") -> {
                    val center = element.getJSONObject("center")
                    lat = center.getDouble("lat")
                    lon = center.getDouble("lon")
                }
                else -> continue
            }

            val tollPoint = GpsPoint(lat, lon, 0L)
            val crossedIt = route.any { point ->
                haversineDistanceMiles(point, tollPoint) * METERS_PER_MILE <= PROXIMITY_METERS
            }
            if (crossedIt) {
                val name = element.optJSONObject("tags")?.optString("name")?.takeIf { it.isNotBlank() }
                return@withContext TollDetectionResult(name ?: "")
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}

private fun postOverpassQuery(query: String): String? {
    val connection = URL(OVERPASS_URL).openConnection() as HttpURLConnection
    return try {
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.connectTimeout = 10_000
        connection.readTimeout = 15_000
        connection.outputStream.use {
            it.write("data=${URLEncoder.encode(query, "UTF-8")}".toByteArray(Charsets.UTF_8))
        }
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            null
        }
    } finally {
        connection.disconnect()
    }
}
