package com.tuusuario.mileagetracker.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.repository.TripRepository
import com.tuusuario.mileagetracker.location.LocationTracker
import com.tuusuario.mileagetracker.util.GpsPoint
import com.tuusuario.mileagetracker.util.calculateTotalDistance
import com.tuusuario.mileagetracker.util.filterGpsNoise
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * HomeScreenState.kt (dentro de HomeViewModel)
 * -----------------------------------------------------------------------
 * Representa TODO lo que la pantalla Home necesita mostrar en un momento
 * dado. En vez de tener 5 variables sueltas, agrupamos el estado en una
 * sola clase inmutable — un patrón muy común en Compose ("UI State").
 * -----------------------------------------------------------------------
 */
data class HomeUiState(
    val isTracking: Boolean = false,
    val currentMiles: Double = 0.0,
    val note: String = "",
    val monthMiles: Double = 0.0,
    val monthDeduction: Double = 0.0,
    val errorMessage: String? = null,
)

/**
 * HomeViewModel.kt
 * -----------------------------------------------------------------------
 * El "cerebro" de la pantalla Home. Aquí vive TODA la lógica: iniciar y
 * detener el GPS, guardar el viaje en la base de datos, calcular el
 * resumen del mes. La pantalla (HomeScreen.kt) solo dibuja lo que el
 * ViewModel le entrega — no toma decisiones por sí misma.
 *
 * Extiende AndroidViewModel (en vez de ViewModel simple) porque
 * necesitamos el "Application context" para inicializar la base de
 * datos y el GPS.
 * -----------------------------------------------------------------------
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository
    private val locationTracker = LocationTracker(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var routePoints = mutableListOf<GpsPoint>()
    private var trackingJob: Job? = null
    private var startTimeMillis: Long = 0

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)
        loadMonthSummary()
    }

    fun updateNote(newNote: String) {
        _uiState.value = _uiState.value.copy(note = newNote)
    }

    /** Se llama cuando el usuario presiona "Start Work". */
    fun startTracking() {
        routePoints = mutableListOf()
        startTimeMillis = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(isTracking = true, currentMiles = 0.0, errorMessage = null)

        // viewModelScope: las corrutinas lanzadas aquí se cancelan
        // automáticamente si el ViewModel se destruye (evita fugas de memoria).
        trackingJob = viewModelScope.launch {
            locationTracker.trackLocation().collect { point ->
                routePoints.add(point)
                val cleanRoute = filterGpsNoise(routePoints)
                val miles = calculateTotalDistance(cleanRoute)
                _uiState.value = _uiState.value.copy(currentMiles = miles)
            }
        }
    }

    /** Se llama cuando el usuario presiona "Stop Work". Guarda el viaje. */
    fun stopTracking(onSaved: (Double) -> Unit) {
        trackingJob?.cancel()
        trackingJob = null

        val cleanRoute = filterGpsNoise(routePoints)
        val miles = calculateTotalDistance(cleanRoute)

        _uiState.value = _uiState.value.copy(isTracking = false)

        if (miles < 0.05) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No se detectó suficiente distancia recorrida. El viaje no fue guardado."
            )
            return
        }

        viewModelScope.launch {
            val trip = TripEntity(
                startTimeMillis = startTimeMillis,
                endTimeMillis = System.currentTimeMillis(),
                miles = miles,
                note = _uiState.value.note.trim(),
                routeJson = routeToJson(cleanRoute)
            )
            repository.saveTrip(trip)
            _uiState.value = _uiState.value.copy(note = "")
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
                com.tuusuario.mileagetracker.util.calculateDeduction(it.miles, java.util.Date(it.startTimeMillis)).deduction
            }
            _uiState.value = _uiState.value.copy(monthMiles = totalMiles, monthDeduction = totalDeduction)
        }
    }

    /** Convierte la ruta GPS a un texto JSON simple para guardarla en la base de datos. */
    private fun routeToJson(route: List<GpsPoint>): String {
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
