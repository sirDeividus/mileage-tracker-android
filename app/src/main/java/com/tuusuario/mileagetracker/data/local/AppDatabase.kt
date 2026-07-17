package com.tuusuario.mileagetracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase.kt
 * -----------------------------------------------------------------------
 * Punto de entrada a la base de datos SQLite local. Usamos el patrón
 * "Singleton" (getInstance) para asegurarnos de que exista UNA sola
 * instancia de la base de datos en toda la app, sin importar cuántas
 * pantallas la usen.
 * -----------------------------------------------------------------------
 */
@Database(entities = [TripEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mileage_tracker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
