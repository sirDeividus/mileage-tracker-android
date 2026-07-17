package com.tuusuario.mileagetracker.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * HistoryViewModel.kt
 * -----------------------------------------------------------------------
 * Expone la lista de viajes como StateFlow. Gracias a que el DAO devuelve
 * un Flow (ver TripDao.kt), la lista se actualiza SOLA en la pantalla
 * cada vez que se inserta o elimina un viaje — no hace falta "recargar"
 * manualmente como sí era necesario en la versión AsyncStorage.
 * -----------------------------------------------------------------------
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    val trips: StateFlow<List<TripEntity>>

    init {
        val dao = AppDatabase.getInstance(application).tripDao()
        repository = TripRepository(dao)
        trips = repository.allTrips.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun deleteTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
