package com.tuusuario.mileagetracker.ui.summary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.repository.TripRepository
import com.tuusuario.mileagetracker.util.calculateDeduction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

enum class Period { MONTH, QUARTER, YEAR }

data class SummaryUiState(
    val period: Period = Period.MONTH,
    val totalMiles: Double = 0.0,
    val totalDeduction: Double = 0.0,
    val tripCount: Int = 0,
)

/**
 * SummaryViewModel.kt
 * -----------------------------------------------------------------------
 * Calcula el resumen fiscal (millas + deducción) filtrando los viajes
 * según el período elegido (mes, trimestre, año). Reacciona
 * automáticamente cuando el usuario cambia de período.
 * -----------------------------------------------------------------------
 */
class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository
    private var allTripsCache: List<TripEntity> = emptyList()

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)

        viewModelScope.launch {
            repository.allTrips.collect { trips ->
                allTripsCache = trips
                recalculate()
            }
        }
    }

    fun setPeriod(period: Period) {
        _uiState.value = _uiState.value.copy(period = period)
        recalculate()
    }

    private fun recalculate() {
        val filtered = allTripsCache.filter { isInPeriod(it.startTimeMillis, _uiState.value.period) }
        val totalMiles = filtered.sumOf { it.miles }
        val totalDeduction = filtered.sumOf { calculateDeduction(it.miles, Date(it.startTimeMillis)).deduction }

        _uiState.value = _uiState.value.copy(
            totalMiles = totalMiles,
            totalDeduction = totalDeduction,
            tripCount = filtered.size
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
