package com.tuusuario.mileagetracker.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import com.tuusuario.mileagetracker.data.backup.BackupManager
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.data.repository.TripRepository
import com.tuusuario.mileagetracker.util.UsState
import com.tuusuario.mileagetracker.util.findStateByEnglishName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

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

    /**
     * NUEVO: detecta automáticamente en qué estado de EE.UU. está el
     * usuario usando el GPS del teléfono + Geocoder (convierte
     * coordenadas en un nombre de lugar), y lo guarda como su estado
     * elegido en Ajustes. Requiere que el permiso de ubicación ya haya
     * sido concedido (se verifica primero, para no reventar en tiempo
     * de ejecución si el usuario nunca activó el rastreo).
     */
    @SuppressLint("MissingPermission")
    fun detectStateFromGps(onResult: (DetectStateResult) -> Unit) {
        val context = getApplication<Application>()
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            onResult(DetectStateResult.PermissionDenied)
            return
        }

        viewModelScope.launch {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val location = withContext(Dispatchers.IO) {
                    // Primero probamos la última ubicación conocida (instantánea);
                    // si no hay ninguna todavía, pedimos una lectura fresca.
                    Tasks.await(fusedClient.lastLocation)
                        ?: Tasks.await(
                            fusedClient.getCurrentLocation(
                                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                                CancellationTokenSource().token
                            )
                        )
                }

                if (location == null) {
                    onResult(DetectStateResult.NotFound)
                    return@launch
                }

                val state = withContext(Dispatchers.IO) {
                    // Locale.US fuerza a que el Geocoder devuelva el nombre del
                    // estado en inglés (ej. "North Carolina"), que es como está
                    // guardado en UsState.enName — independiente del idioma
                    // elegido en la app.
                    @Suppress("DEPRECATION")
                    val addresses = Geocoder(context, Locale.US)
                        .getFromLocation(location.latitude, location.longitude, 1)
                    val adminArea = addresses?.firstOrNull()?.adminArea
                    adminArea?.let { findStateByEnglishName(it) }
                }

                if (state != null) {
                    UserPreferences(context).stateCode = state.code
                    onResult(DetectStateResult.Found(state))
                } else {
                    onResult(DetectStateResult.NotFound)
                }
            } catch (e: Exception) {
                onResult(DetectStateResult.NotFound)
            }
        }
    }
}

sealed class DetectStateResult {
    data class Found(val state: UsState) : DetectStateResult()
    object NotFound : DetectStateResult()
    object PermissionDenied : DetectStateResult()
}
