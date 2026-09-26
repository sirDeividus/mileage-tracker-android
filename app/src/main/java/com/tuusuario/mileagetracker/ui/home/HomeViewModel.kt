package com.tuusuario.mileagetracker.ui.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.data.repository.TripRepository
import com.tuusuario.mileagetracker.location.TrackingService
import com.tuusuario.mileagetracker.location.TrackingSessionState
import com.tuusuario.mileagetracker.util.DeliveryPlatform
import com.tuusuario.mileagetracker.util.GpsPoint
import com.tuusuario.mileagetracker.util.MIN_TRACKABLE_MILES
import com.tuusuario.mileagetracker.util.TollDetectionResult
import com.tuusuario.mileagetracker.util.calculateDeduction
import com.tuusuario.mileagetracker.util.detectTollAlongRoute
import com.tuusuario.mileagetracker.util.stringsFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

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
    // NUEVO v2.4: millas de un viaje que quedó sin cerrar de una sesión
    // anterior (se le olvidó presionar "Stop Work") y que se acaba de
    // recuperar automáticamente. null = no hay nada que avisar.
    val recoveredTripMiles: Double? = null,
    // NUEVO v2.5: se llena cuando TollDetector.kt encuentra que la ruta
    // del viaje que acabas de terminar pasó cerca de una caseta de peaje
    // conocida — dispara el diálogo para que confirmes el monto.
    val tollDetection: TollDetectionResult? = null,
)

/**
 * HomeViewModel.kt  (ACTUALIZADO v2.4)
 * -----------------------------------------------------------------------
 * CAMBIO CLAVE respecto a la v1.0: este ViewModel YA NO escucha el GPS
 * directamente ni escribe el viaje en la base de datos al presionar "Stop
 * Work". Eso ahora lo hace TrackingService de principio a fin (ver
 * TrackingService.kt): crea la fila del viaje apenas empieza, la va
 * actualizando cada ~20s mientras rastrea (autoguardado), y la cierra al
 * recibir ACTION_STOP — así ningún dato depende de que esta pantalla siga
 * viva.
 *
 * Este ViewModel solo:
 *   1. Le ordena a TrackingService que empiece/termine (con un Intent).
 *   2. OBSERVA el progreso a través de TrackingSessionState.
 *   3. Muestra los totales del mes de forma REACTIVA (Flow), para que se
 *      actualicen solos apenas el Service termina de guardar — sin tener
 *      que "recargar" manualmente.
 *   4. Al abrir la app, revisa si quedó un viaje sin cerrar de una sesión
 *      anterior (se le olvidó presionar "Stop Work") y lo recupera.
 * -----------------------------------------------------------------------
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository
    private val strings = stringsFor(UserPreferences(application).language)

    // NUEVO v2.5: id del viaje que está esperando que el usuario confirme
    // (o descarte) el peaje detectado por GPS — no hace falta exponerlo en
    // uiState, es plomería interna entre stopTracking() y confirmDetectedToll().
    private var pendingTollTripId: Long = 0

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)

        // NUEVO v2.4: los totales del mes ahora se recalculan solos cada
        // vez que cambia la tabla de viajes (autoguardado, edición,
        // eliminación desde el Historial, etc.) — ya no hace falta pedirle
        // explícitamente al ViewModel que "recargue".
        viewModelScope.launch {
            repository.allTrips.collect { trips -> applyMonthSummary(trips) }
        }

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

        recoverOrphanedTrip()
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
        // NUEVO v2.4: también se guarda en TrackingSessionState, que es lo
        // que TrackingService lee para el autoguardado del viaje en curso.
        TrackingSessionState.tollAmountText = text
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

    /**
     * Se llama cuando el usuario presiona "Stop Work". El guardado real lo
     * hace TrackingService (ver ACTION_STOP) — aquí solo reflejamos el
     * cambio de inmediato en la pantalla para que se sienta instantáneo.
     */
    fun stopTracking(onSaved: (Double) -> Unit) {
        // NUEVO v2.5: capturamos la ruta y el id del viaje ANTES de mandar
        // ACTION_STOP — el Service va a resetear TrackingSessionState de
        // forma asíncrona apenas termine de cerrar el viaje, así que hay
        // que leer estos valores ahora mismo.
        val route = TrackingSessionState.routePoints.value
        val tripId = TrackingSessionState.activeTripId

        val context = getApplication<Application>()
        val stopIntent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }
        context.startService(stopIntent)

        val miles = TrackingSessionState.currentMiles.value
        if (miles < MIN_TRACKABLE_MILES) {
            _uiState.value = _uiState.value.copy(errorMessage = strings.tripTooShortError)
        } else {
            onSaved(miles)
            checkForTollAlongRoute(route, tripId)
        }

        _uiState.value = _uiState.value.copy(
            isTracking = false,
            currentMiles = 0.0,
            selectedPlatformId = "",
            customPlatformName = "",
            tollAmountText = "",
        )
    }

    /**
     * NUEVO v2.5: consulta OpenStreetMap (mejor esfuerzo, no bloquea nada)
     * para ver si el viaje que se acaba de guardar pasó por una caseta de
     * peaje conocida. Si detecta algo, dispara el diálogo de confirmación
     * — ver TollDetectedDialog.kt.
     */
    private fun checkForTollAlongRoute(route: List<GpsPoint>, tripId: Long) {
        if (tripId == 0L) return
        viewModelScope.launch {
            val result = detectTollAlongRoute(route) ?: return@launch
            pendingTollTripId = tripId
            _uiState.value = _uiState.value.copy(tollDetection = result)
        }
    }

    /** NUEVO v2.5: el usuario confirmó cuánto pagó en el peaje detectado. */
    fun confirmDetectedToll(amount: Double) {
        val tripId = pendingTollTripId
        _uiState.value = _uiState.value.copy(tollDetection = null)
        if (tripId == 0L) return
        viewModelScope.launch {
            repository.addTollToTrip(tripId, amount)
        }
    }

    /** NUEVO v2.5: el usuario dijo que no pagó nada (o cerró el diálogo). */
    fun dismissDetectedToll() {
        pendingTollTripId = 0
        _uiState.value = _uiState.value.copy(tollDetection = null)
    }

    /** NUEVO v2.4: descarta el aviso de "viaje recuperado" una vez que el usuario lo vio. */
    fun dismissRecoveredTripNotice() {
        _uiState.value = _uiState.value.copy(recoveredTripMiles = null)
    }

    /**
     * NUEVO v2.4: si la app se cerró (o el teléfono se reinició) mientras
     * un viaje seguía rastreándose y nunca se presionó "Stop Work", queda
     * una fila "borrador" (isActive = true) en la base de datos con las
     * últimas millas que TrackingService alcanzó a guardar. La cerramos
     * acá para que no se pierda ni quede fantasma para siempre.
     */
    private fun recoverOrphanedTrip() {
        // Si TrackingSessionState.isTracking ya es true, es porque el
        // Service SÍ sigue corriendo en este mismo proceso (la app solo se
        // minimizó, no se cerró) — ahí no hay nada que recuperar.
        if (TrackingSessionState.isTracking.value) return

        viewModelScope.launch {
            val orphan = repository.getActiveTrip() ?: return@launch
            repository.updateTrip(orphan.copy(isActive = false))
            _uiState.value = _uiState.value.copy(recoveredTripMiles = orphan.miles)
        }
    }

    private fun applyMonthSummary(trips: List<TripEntity>) {
        val now = Calendar.getInstance()
        val thisMonthTrips = trips.filter { trip ->
            val cal = Calendar.getInstance().apply { timeInMillis = trip.startTimeMillis }
            cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        }
        val totalMiles = thisMonthTrips.sumOf { it.miles }
        val totalDeduction = thisMonthTrips.sumOf {
            calculateDeduction(it.miles, Date(it.startTimeMillis)).deduction
        }
        val totalTolls = thisMonthTrips.sumOf { it.tollAmount }
        _uiState.value = _uiState.value.copy(monthMiles = totalMiles, monthDeduction = totalDeduction, monthTolls = totalTolls)
    }
}
