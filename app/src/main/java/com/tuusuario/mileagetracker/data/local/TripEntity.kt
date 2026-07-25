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
 *
 * CAMBIO EN v2.0: se agregó el campo "platform" (DoorDash, Uber, etc.).
 * Ver AppDatabase.kt -> MIGRATION_1_2 para entender cómo se agregó esta
 * columna SIN borrar los viajes que el usuario ya tenía guardados.
 *
 * CAMBIO EN v2.3: se agregó "tollAmount" (peajes). El IRS permite
 * deducir peajes y estacionamiento de negocio POR SEPARADO, además de
 * la deducción estándar por millaje — no están incluidos en la tasa por
 * milla. Ver SummaryScreen.kt para cómo se suman ambos en el resumen
 * fiscal. Ver AppDatabase.kt -> MIGRATION_2_3.
 * -----------------------------------------------------------------------
 */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val startTimeMillis: Long,   // fecha/hora de inicio del viaje (timestamp)
    val endTimeMillis: Long,     // fecha/hora de fin del viaje
    val miles: Double,           // distancia total recorrida
    val note: String,            // nota adicional opcional (v1.0, se mantiene)
    val routeJson: String,       // ruta GPS completa, guardada como texto JSON

    // NUEVO v2.0: id de la plataforma elegida (ej. "doordash", "uber_eats",
    // o el nombre libre que el usuario escribió si eligió "Otra").
    val platform: String = "",

    // NUEVO v2.3: monto pagado en peajes durante este viaje, en dólares.
    val tollAmount: Double = 0.0,
)
