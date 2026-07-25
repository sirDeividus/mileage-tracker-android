package com.tuusuario.mileagetracker.location

import com.tuusuario.mileagetracker.util.GpsPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TrackingSessionState.kt  (NUEVO en v2.0)
 * -----------------------------------------------------------------------
 * Problema que resuelve: antes, el GPS vivía DENTRO del ViewModel de la
 * pantalla Home. Cuando el usuario minimizaba la app o apagaba la
 * pantalla, Android "congelaba" el ViewModel y las lecturas de GPS se
 * detenían o se hacían muy espaciadas -> millas mal contadas.
 *
 * La solución es mover el rastreo real a un Foreground Service
 * (TrackingService.kt), que Android SÍ mantiene vivo en segundo plano
 * porque tiene una notificación visible. Pero el Service y la pantalla
 * (HomeViewModel) son dos componentes separados que no pueden pasarse
 * variables directamente — por eso usamos este "object" (singleton):
 * un punto de datos compartido que ambos pueden leer y escribir mientras
 * el proceso de la app siga vivo.
 * -----------------------------------------------------------------------
 */
object TrackingSessionState {

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _currentMiles = MutableStateFlow(0.0)
    val currentMiles: StateFlow<Double> = _currentMiles.asStateFlow()

    private val _routePoints = MutableStateFlow<List<GpsPoint>>(emptyList())
    val routePoints: StateFlow<List<GpsPoint>> = _routePoints.asStateFlow()

    var startTimeMillis: Long = 0
        private set

    var selectedPlatform: String = ""
    var customPlatformName: String = ""

    fun begin() {
        _routePoints.value = emptyList()
        _currentMiles.value = 0.0
        startTimeMillis = System.currentTimeMillis()
        _isTracking.value = true
    }

    fun addPoint(point: GpsPoint, updatedMiles: Double) {
        _routePoints.value = _routePoints.value + point
        _currentMiles.value = updatedMiles
    }

    /** Se llama al terminar el viaje (guardado o descartado) para dejar todo listo para el próximo. */
    fun reset() {
        _isTracking.value = false
        _routePoints.value = emptyList()
        _currentMiles.value = 0.0
        selectedPlatform = ""
        customPlatformName = ""
    }
}
