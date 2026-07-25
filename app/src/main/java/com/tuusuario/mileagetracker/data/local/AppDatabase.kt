package com.tuusuario.mileagetracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * AppDatabase.kt
 * -----------------------------------------------------------------------
 * Punto de entrada a la base de datos SQLite local. Usamos el patrón
 * "Singleton" (getInstance) para asegurarnos de que exista UNA sola
 * instancia de la base de datos en toda la app, sin importar cuántas
 * pantallas la usen.
 *
 * MUY IMPORTANTE (v2.0) — CÓMO NO PERDER LOS DATOS DEL USUARIO:
 * Cuando cambiamos la estructura de una tabla (agregamos la columna
 * "platform" en TripEntity), Room necesita que le expliquemos CÓMO
 * transformar la base de datos vieja en la nueva. Si no le decimos nada,
 * por defecto Room BORRA toda la base de datos y la crea de cero — eso
 * es exactamente lo que NO queremos.
 *
 * La solución es declarar una "Migration": un paso a paso en SQL puro
 * que Room ejecuta automáticamente la primera vez que el usuario abre
 * la app después de instalar la actualización. Aquí, MIGRATION_1_2
 * simplemente agrega la nueva columna "platform" con un valor por
 * defecto vacío, dejando todos los viajes existentes intactos.
 * -----------------------------------------------------------------------
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE trips ADD COLUMN platform TEXT NOT NULL DEFAULT ''"
        )
    }
}

@Database(entities = [TripEntity::class], version = 2, exportSchema = false)
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
                )
                    // Registramos TODAS las migraciones conocidas. Si en el
                    // futuro agregas la v3, se agrega aquí MIGRATION_2_3 sin
                    // quitar esta línea, para que usuarios que vengan desde
                    // muy atrás (v1 -> v3) sigan sin perder datos.
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
