package com.tuusuario.mileagetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TripEntity.kt
 * -----------------------------------------------------------------------
 * Representa la tabla "trips" dentro de la base de datos SQLite local.
 * Room convierte esta clase automáticamente en una tabla de base de datos:
 * cada propiedad = una columna.
 *
 * Aprendizaje clave: en Room, @Entity marca la clase como tabla,
 * @PrimaryKey marca el identificador único de cada fila.
 * -----------------------------------------------------------------------
 */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val startTimeMillis: Long,   // fecha/hora de inicio del viaje (timestamp)
    val endTimeMillis: Long,     // fecha/hora de fin del viaje
    val miles: Double,           // distancia total recorrida
    val note: String,            // motivo del viaje (ej. "Visita a cliente")
    val routeJson: String        // ruta GPS completa, guardada como texto JSON
)
