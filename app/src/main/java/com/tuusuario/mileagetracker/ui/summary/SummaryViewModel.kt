package com.tuusuario.mileagetracker.ui.summary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.data.repository.TripRepository
import com.tuusuario.mileagetracker.util.UsState
import com.tuusuario.mileagetracker.util.calculateDeduction
import com.tuusuario.mileagetracker.util.findStateByCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

enum class Period { MONTH, QUARTER, YEAR }

// NUEVO: totales de un período agrupados por plataforma de trabajo
// (DoorDash, Uber, Amazon Flex, etc.), para responder "¿cuánto manejé
// y cuánto puedo deducir por cada app?".
data class PlatformTotal(
    val platformId: String,
    val miles: Double,
    val deduction: Double,
    val tripCount: Int,
)

data class SummaryUiState(
    val period: Period = Period.MONTH,
    val totalMiles: Double = 0.0,
    val totalDeduction: Double = 0.0,      // solo la deducción por millaje
    val totalTolls: Double = 0.0,          // NUEVO v2.3
    val combinedDeduction: Double = 0.0,   // NUEVO v2.3: millaje + peajes
    val tripCount: Int = 0,
    val selectedState: UsState? = null, // NUEVO: reemplaza el "North Carolina" fijo
    val platformBreakdown: List<PlatformTotal> = emptyList(), // NUEVO
)

/**
 * SummaryViewModel.kt  (ACTUALIZADO)
 * -----------------------------------------------------------------------
 * Calcula el resumen fiscal (millas + deducción) filtrando los viajes
 * según el período elegido (mes, trimestre, año). Reacciona
 * automáticamente cuando el usuario cambia de período.
 *
 * CAMBIO: antes las notas de la parte inferior estaban fijas a "North
 * Carolina". Ahora se lee el estado que el usuario eligió en Ajustes
 * (UserPreferences.stateCode) y se usa esa información — válido para
 * cualquiera de los 50 estados.
 * -----------------------------------------------------------------------
 */
class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository
    private val prefs = UserPreferences(application)
    private var allTripsCache: List<TripEntity> = emptyList()

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)

        refreshSelectedState()

        viewModelScope.launch {
            repository.allTrips.collect { trips ->
                allTripsCache = trips
                recalculate()
            }
        }
    }

    /** Se llama cada vez que la pantalla vuelve al frente, por si el usuario cambió el estado en Ajustes. */
    fun refreshSelectedState() {
        val state = findStateByCode(prefs.stateCode)
        _uiState.value = _uiState.value.copy(selectedState = state)
    }

    fun setPeriod(period: Period) {
        _uiState.value = _uiState.value.copy(period = period)
        recalculate()
    }

    private fun recalculate() {
        val filtered = allTripsCache.filter { isInPeriod(it.startTimeMillis, _uiState.value.period) }
        val totalMiles = filtered.sumOf { it.miles }
        val totalDeduction = filtered.sumOf { calculateDeduction(it.miles, Date(it.startTimeMillis)).deduction }
        // NUEVO v2.3: el IRS permite deducir peajes de negocio POR SEPARADO
        // de la deducción por millaje, así que se suman al final.
        val totalTolls = filtered.sumOf { it.tollAmount }

        // NUEVO: agrupa por plataforma para mostrar cuánto se manejó/dedujo
        // en cada app (DoorDash, Uber, etc.), ordenado de mayor a menor millaje.
        val platformBreakdown = filtered
            .groupBy { it.platform.ifBlank { "" } }
            .map { (platformId, trips) ->
                PlatformTotal(
                    platformId = platformId,
                    miles = trips.sumOf { it.miles },
                    deduction = trips.sumOf { calculateDeduction(it.miles, Date(it.startTimeMillis)).deduction },
                    tripCount = trips.size,
                )
            }
            .sortedByDescending { it.miles }

        _uiState.value = _uiState.value.copy(
            totalMiles = totalMiles,
            totalDeduction = totalDeduction,
            totalTolls = totalTolls,
            combinedDeduction = totalDeduction + totalTolls,
            tripCount = filtered.size,
            platformBreakdown = platformBreakdown,
        )
    }

    private fun isInPeriod(millis: Long, period: Period): Boolean {
        val now = Calendar.getInstance()
        val tripDate = Calendar.getInstance().apply { timeInMillis = millis }

        return when (period) {
            Period.MONTH -> tripDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                tripDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            Period.QUARTER -> (tripDate.get(Calendar.MONTH) / 3) == (now.get(Calendar.MONTH) / 3) &&
                tripDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            Period.YEAR -> tripDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        }
    }
}
