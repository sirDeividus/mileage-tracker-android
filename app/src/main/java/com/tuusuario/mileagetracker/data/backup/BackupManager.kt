package com.tuusuario.mileagetracker.data.backup

import android.content.Context
import android.net.Uri
import com.tuusuario.mileagetracker.data.local.TripEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * BackupManager.kt
 * -----------------------------------------------------------------------
 * Exporta e importa TODOS los viajes guardados como un archivo de texto
 * JSON, que el usuario elige dónde guardar (Google Drive, Descargas,
 * correo, etc. — usamos el selector de archivos nativo de Android, el
 * "Storage Access Framework", así que la app NO necesita permisos
 * especiales de almacenamiento).
 *
 * Por qué esto es importante: el "Auto Backup" de Android (ver
 * backup_rules.xml) es automático pero NO es instantáneo — Android
 * decide cuándo correrlo (normalmente necesita wifi + batería cargando
 * + horas de inactividad). Si el usuario desinstala la app antes de que
 * el backup automático haya corrido, pierde los datos. Exportar/Importar
 * manual le da control total: puede hacerlo justo antes de desinstalar,
 * o guardar respaldos periódicos él mismo.
 * -----------------------------------------------------------------------
 */
object BackupManager {

    private const val FORMAT_VERSION = 1

    /**
     * Convierte la lista de viajes a un texto JSON y lo escribe en el
     * archivo que el usuario eligió (uri viene de un Intent
     * ACTION_CREATE_DOCUMENT, ver SettingsScreen.kt).
     */
    fun exportToUri(context: Context, uri: Uri, trips: List<TripEntity>): Boolean {
        return try {
            val json = tripsToJson(trips)
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(json.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Lee un archivo de respaldo (elegido con ACTION_OPEN_DOCUMENT) y lo
     * convierte de vuelta en una lista de TripEntity, lista para
     * insertar en Room.
     */
    fun importFromUri(context: Context, uri: Uri): List<TripEntity>? {
        return try {
            val text = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            } ?: return null
            jsonToTrips(text)
        } catch (e: Exception) {
            null
        }
    }

    private fun tripsToJson(trips: List<TripEntity>): String {
        val root = JSONObject()
        root.put("formatVersion", FORMAT_VERSION)
        root.put("exportedAtMillis", System.currentTimeMillis())

        val array = JSONArray()
        trips.forEach { trip ->
            val obj = JSONObject()
            obj.put("startTimeMillis", trip.startTimeMillis)
            obj.put("endTimeMillis", trip.endTimeMillis)
            obj.put("miles", trip.miles)
            obj.put("note", trip.note)
            obj.put("routeJson", trip.routeJson)
            obj.put("platform", trip.platform)
            obj.put("tollAmount", trip.tollAmount)
            array.put(obj)
        }
        root.put("trips", array)
        return root.toString()
    }

    private fun jsonToTrips(text: String): List<TripEntity> {
        val root = JSONObject(text)
        val array = root.getJSONArray("trips")
        val result = mutableListOf<TripEntity>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                TripEntity(
                    // id = 0 para que Room le asigne un id nuevo y no choque
                    // con viajes que ya existan en el dispositivo actual.
                    id = 0,
                    startTimeMillis = obj.getLong("startTimeMillis"),
                    endTimeMillis = obj.getLong("endTimeMillis"),
                    miles = obj.getDouble("miles"),
                    note = obj.optString("note", ""),
                    routeJson = obj.optString("routeJson", "[]"),
                    platform = obj.optString("platform", ""),
                    tollAmount = obj.optDouble("tollAmount", 0.0),
                )
            )
        }
        return result
    }
}
