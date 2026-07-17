package com.tuusuario.mileagetracker.data.repository

import com.tuusuario.mileagetracker.data.local.TripDao
import com.tuusuario.mileagetracker.data.local.TripEntity
import kotlinx.coroutines.flow.Flow

/**
 * TripRepository.kt
 * -----------------------------------------------------------------------
 * Patrón "Repository": es la ÚNICA puerta de entrada para que las
 * pantallas (ViewModels) accedan a los datos de viajes. Ninguna pantalla
 * llama a Room directamente.
 *
 * ¿Por qué? Si mañana quieres agregar sincronización en la nube (ej.
 * Firebase), solo tocas este archivo — las pantallas ni se enteran del
 * cambio, porque ellas solo conocen esta interfaz.
 * -----------------------------------------------------------------------
 */
class TripRepository(private val dao: TripDao) {

    val allTrips: Flow<List<TripEntity>> = dao.getAllTrips()

    suspend fun saveTrip(trip: TripEntity) {
        dao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: TripEntity) {
        dao.deleteTrip(trip)
    }

    suspend fun clearAll() {
        dao.clearAllTrips()
    }
}
