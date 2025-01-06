package si.travelbuddy.travelbuddy.ui.stops

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import si.travelbuddy.travelbuddy.model.Departure
import si.travelbuddy.travelbuddy.model.Stop

data class StopsUiState(
    val currentStop: Stop? = null,
    val loadedDepartures: Boolean = false,
    val currentStopDepartures: List<Departure> = listOf()
)

class StopsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StopsUiState())
    val uiState: StateFlow<StopsUiState> = _uiState.asStateFlow()

    fun updateStop(stop: Stop?) {
        _uiState.value = StopsUiState(
            currentStop = stop,
            loadedDepartures = false
        )
    }

    fun updateStopDepartures(deps: List<Departure>) {
        val curStop = uiState.value.currentStop
        _uiState.value = StopsUiState(
            currentStop = curStop,
            loadedDepartures = true,
            currentStopDepartures = deps
        )
    }

    init {
        _uiState.value = StopsUiState()
    }
}