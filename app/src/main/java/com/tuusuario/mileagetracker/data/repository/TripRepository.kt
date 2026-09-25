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

    // Devuelve el id generado — TrackingService lo necesita para saber a
    // qué fila seguir escribiendo mientras el viaje está en curso.
    suspend fun saveTrip(trip: TripEntity): Long = dao.insertTrip(trip)

    // NUEVO v2.4: busca un viaje que quedó "en curso" (isActive = true) de
    // una sesión de rastreo anterior que nunca se cerró — ver
    // HomeViewModel.recoverOrphanedTrip().
    suspend fun getActiveTrip(): TripEntity? = dao.getActiveTrip()

    // NUEVO v2.3: usado por la función de "Importar respaldo".
    suspend fun saveAll(trips: List<TripEntity>) {
        dao.insertAll(trips)
    }

    // NUEVO: usado por "Editar" y por "Agregar viaje manual" en el Historial.
    suspend fun updateTrip(trip: TripEntity) {
        dao.updateTrip(trip)
    }

    suspend fun deleteTrip(trip: TripEntity) {
        dao.deleteTrip(trip)
    }

    suspend fun clearAll() {
        dao.clearAllTrips()
    }
}
