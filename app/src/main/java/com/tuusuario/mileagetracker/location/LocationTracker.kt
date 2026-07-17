package com.tuusuario.mileagetracker.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tuusuario.mileagetracker.util.GpsPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * LocationTracker.kt
 * -----------------------------------------------------------------------
 * Encapsula toda la comunicación con el GPS del teléfono usando
 * FusedLocationProviderClient (la API recomendada por Google, más
 * precisa y eficiente en batería que el GPS "crudo" de Android).
 *
 * Expone un Flow<GpsPoint>: un "flujo" de puntos GPS que la pantalla
 * puede ir escuchando en tiempo real mientras el usuario conduce.
 * Esto es equivalente a lo que hacía useLocationTracker.js con
 * Location.watchPositionAsync() en la versión Expo.
 * -----------------------------------------------------------------------
 */
class LocationTracker(context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000L // pedir una nueva lectura cada 5 segundos
    ).setMinUpdateDistanceMeters(10f) // o cada 10 metros recorridos
        .build()

    /**
     * Empieza a escuchar actualizaciones de ubicación. Se debe llamar
     * SOLO después de haber verificado que el permiso ACCESS_FINE_LOCATION
     * fue concedido por el usuario (ver PermissionUtils / MainActivity).
     */
    @SuppressLint("MissingPermission")
    fun trackLocation(): Flow<GpsPoint> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(
                        GpsPoint(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timestampMillis = location.time
                        )
                    )
                }
            }
        }

        fusedClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

        // Cuando el Flow se cancela (el usuario presiona "Stop Work" y
        // dejamos de escuchar), removemos el callback para no gastar batería.
        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }
}
