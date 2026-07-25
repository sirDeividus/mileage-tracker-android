package com.tuusuario.mileagetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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

    @Insert
    suspend fun insertTrip(trip: TripEntity)

    // NUEVO v2.3: inserta varios viajes de una sola vez — se usa al
    // importar un archivo de respaldo (ver BackupManager.kt).
    @Insert
    suspend fun insertAll(trips: List<TripEntity>)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips ORDER BY startTimeMillis DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("DELETE FROM trips")
    suspend fun clearAllTrips()
}
