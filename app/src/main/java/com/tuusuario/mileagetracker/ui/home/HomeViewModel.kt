package com.tuusuario.mileagetracker.ui.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.repository.TripRepository
import com.tuusuario.mileagetracker.location.TrackingService
import com.tuusuario.mileagetracker.location.TrackingSessionState
import com.tuusuario.mileagetracker.util.DeliveryPlatform
import com.tuusuario.mileagetracker.util.calculateDeduction
import com.tuusuario.mileagetracker.util.calculateTotalDistance
import com.tuusuario.mileagetracker.util.filterGpsNoise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * HomeUiState.kt (dentro de HomeViewModel)
 * -----------------------------------------------------------------------
 * CAMBIO v2.0: se agregaron selectedPlatformId y customPlatformName para
 * el nuevo selector de plataformas de trabajo.
 * -----------------------------------------------------------------------
 */
data class HomeUiState(
    val isTracking: Boolean = false,
    val currentMiles: Double = 0.0,
    val selectedPlatformId: String = "",
    val customPlatformName: String = "",
    val tollAmountText: String = "",   // NUEVO v2.3: peajes del viaje en curso
    val monthMiles: Double = 0.0,
    val monthDeduction: Double = 0.0,
    val monthTolls: Double = 0.0,      // NUEVO v2.3
    val errorMessage: String? = null,
)

/**
 * HomeViewModel.kt  (REESCRITO en v2.0)
 * -----------------------------------------------------------------------
 * CAMBIO CLAVE respecto a la v1.0: este ViewModel YA NO escucha el GPS
 * directamente. Antes lo hacía con locationTracker.trackLocation(),
 * pero eso se detenía cuando Android congelaba la app en segundo plano
 * (el bug reportado: "no me toma las millas cuando el cel está
 * inactivo").
 *
 * Ahora el ViewModel solo:
 *   1. Le ordena a TrackingService que empiece/termine (con un Intent).
 *   2. OBSERVA el progreso a través de TrackingSessionState, que el
 *      Service actualiza desde segundo plano sin depender de que esta
 *      pantalla esté visible.
 *
 * El GPS real vive en el Service, que Android mantiene vivo gracias a
 * la notificación persistente (startForeground).
 * -----------------------------------------------------------------------
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)
        loadMonthSummary()

        // Nos suscribimos al estado que publica el Service en segundo plano
        // y lo reflejamos en nuestro propio uiState para que la pantalla
        // se redibuje automáticamente.
        viewModelScope.launch {
            combine(
                TrackingSessionState.isTracking,
                TrackingSessionState.currentMiles
            ) { tracking, miles -> tracking to miles }
                .collect { (tracking, miles) ->
                    _uiState.value = _uiState.value.copy(isTracking = tracking, currentMiles = miles)
                }
        }
    }

    fun selectPlatform(platform: DeliveryPlatform) {
        TrackingSessionState.selectedPlatform = platform.id
        _uiState.value = _uiState.value.copy(selectedPlatformId = platform.id)
    }

    fun updateCustomPlatformName(name: String) {
        TrackingSessionState.customPlatformName = name
        _uiState.value = _uiState.value.copy(customPlatformName = name)
    }

    /** NUEVO v2.3: actualiza el monto de peajes que el usuario va escribiendo. */
    fun updateTollAmount(text: String) {
        _uiState.value = _uiState.value.copy(tollAmountText = text)
    }

    /** Se llama cuando el usuario presiona "Start Work". */
    fun startTracking() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
        val context = getApplication<Application>()
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START
        }
        // startForegroundService es obligatorio a partir de Android 8 (Oreo)
        // para servicios que van a mostrar una notificación inmediatamente.
        context.startForegroundService(intent)
    }

    /** Se llama cuando el usuario presiona "Stop Work". Guarda el viaje. */
    fun stopTracking(onSaved: (Double) -> Unit) {
        val context = getApplication<Application>()
        val stopIntent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }
        context.startService(stopIntent)

        val route = TrackingSessionState.routePoints.value
        val cleanRoute = filterGpsNoise(route)
        val miles = calculateTotalDistance(cleanRoute)
        val startTime = TrackingSessionState.startTimeMillis
        val platformId = TrackingSessionState.selectedPlatform
        val customName = TrackingSessionState.customPlatformName

        if (miles < 0.05) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No se detectó suficiente distancia recorrida. El viaje no fue guardado."
            )
            TrackingSessionState.reset()
            return
        }

        viewModelScope.launch {
            val platformValue = if (platformId == "other") customName.trim() else platformId
            val tollAmount = _uiState.value.tollAmountText.toDoubleOrNull() ?: 0.0

            val trip = TripEntity(
                startTimeMillis = startTime,
                endTimeMillis = System.currentTimeMillis(),
                miles = miles,
                note = "",
                routeJson = routeToJson(cleanRoute),
                platform = platformValue,
                tollAmount = tollAmount,
            )
            repository.saveTrip(trip)
            TrackingSessionState.reset()
            _uiState.value = _uiState.value.copy(
                selectedPlatformId = "",
                customPlatformName = "",
                tollAmountText = "",
            )
            loadMonthSummary()
            onSaved(miles)
        }
    }

    private fun loadMonthSummary() {
        viewModelScope.launch {
            val trips = repository.allTrips.first()
            val now = java.util.Calendar.getInstance()
            val thisMonthTrips = trips.filter { trip ->
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = trip.startTimeMillis }
                cal.get(java.util.Calendar.MONTH) == now.get(java.util.Calendar.MONTH) &&
                    cal.get(java.util.Calendar.YEAR) == now.get(java.util.Calendar.YEAR)
            }
            val totalMiles = thisMonthTrips.sumOf { it.miles }
            val totalDeduction = thisMonthTrips.sumOf {
                calculateDeduction(it.miles, java.util.Date(it.startTimeMillis)).deduction
            }
            val totalTolls = thisMonthTrips.sumOf { it.tollAmount }
            _uiState.value = _uiState.value.copy(monthMiles = totalMiles, monthDeduction = totalDeduction, monthTolls = totalTolls)
        }
    }

    private fun routeToJson(route: List<com.tuusuario.mileagetracker.util.GpsPoint>): String {
        val array = JSONArray()
        route.forEach { point ->
            val obj = JSONObject()
            obj.put("lat", point.latitude)
            obj.put("lng", point.longitude)
            obj.put("t", point.timestampMillis)
            array.put(obj)
        }
        return array.toString()
    }
}
