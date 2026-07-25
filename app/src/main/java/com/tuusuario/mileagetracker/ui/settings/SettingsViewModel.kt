package com.tuusuario.mileagetracker.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.backup.BackupManager
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.repository.TripRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * SettingsViewModel.kt  (NUEVO v2.3)
 * -----------------------------------------------------------------------
 * Maneja la exportación e importación del respaldo. Sigue el mismo
 * patrón MVVM del resto de la app: la pantalla (SettingsScreen) solo
 * pide "exporta" o "importa" y muestra el resultado — toda la lógica
 * de leer/escribir vive aquí y en BackupManager.kt.
 * -----------------------------------------------------------------------
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)
    }

    fun exportBackup(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val trips = repository.allTrips.first()
            val context = getApplication<Application>()
            val success = BackupManager.exportToUri(context, uri, trips)
            onResult(success)
        }
    }

    fun importBackup(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val importedTrips: List<TripEntity>? = BackupManager.importFromUri(context, uri)
            if (importedTrips == null) {
                onResult(false)
            } else {
                repository.saveAll(importedTrips)
                onResult(true)
            }
        }
    }
}
