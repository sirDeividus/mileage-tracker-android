package com.tuusuario.mileagetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * TripDao.kt
 * -----------------------------------------------------------------------
 * DAO = "Data Access Object". Aquí declaramos QUÉ operaciones de base de
 * datos existen (insertar, borrar, consultar), sin escribir SQL manual
 * en la mayoría de los casos: Room genera el código real por nosotros
 * a partir de estas anotaciones.
 *
 * Flow<List<TripEntity>> significa: "un flujo de datos que se actualiza
 * automáticamente en la UI cada vez que la tabla cambia" — no necesitamos
 * recargar manualmente la lista después de insertar o borrar.
 * -----------------------------------------------------------------------
 */
@Dao
interface TripDao {

    // Devuelve el id autogenerado de la fila insertada — lo necesita
    // TrackingService para saber qué fila seguir actualizando mientras
    // el viaje está en curso (ver MIGRATION_3_4 / isActive en TripEntity).
    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    // NUEVO v2.3: inserta varios viajes de una sola vez — se usa al
    // importar un archivo de respaldo (ver BackupManager.kt).
    @Insert
    suspend fun insertAll(trips: List<TripEntity>)

    // NUEVO: permite editar un viaje ya guardado (plataforma, millas,
    // peajes, nota) — usado por "Editar" en el Historial, y por
    // TrackingService para ir guardando el viaje en curso.
    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    // NUEVO v2.4: usado para descartar un "borrador" de viaje casi sin
    // millas (ej. el usuario presionó Start y Stop casi al instante).
    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTripById(id: Long)

    // Solo viajes YA terminados (isActive = false) — el borrador en curso
    // no debe aparecer en el Historial ni en el Resumen mientras se rastrea.
    @Query("SELECT * FROM trips WHERE isActive = 0 ORDER BY startTimeMillis DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    // NUEVO v2.4: detecta si quedó un viaje "a medias" de una sesión de
    // rastreo anterior que nunca se cerró correctamente (la app se cerró,
    // el teléfono se reinició, etc.) — ver HomeViewModel.recoverOrphanedTrip().
    @Query("SELECT * FROM trips WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveTrip(): TripEntity?

    // NUEVO v2.5: suma el peaje detectado por GPS al que ya tuviera el
    // viaje (en vez de sobrescribirlo), por si el usuario ya había
    // anotado algo a mano mientras manejaba — ver TollDetector.kt.
    @Query("UPDATE trips SET tollAmount = tollAmount + :amount WHERE id = :id")
    suspend fun addToll(id: Long, amount: Double)

    @Query("DELETE FROM trips")
    suspend fun clearAllTrips()
}
