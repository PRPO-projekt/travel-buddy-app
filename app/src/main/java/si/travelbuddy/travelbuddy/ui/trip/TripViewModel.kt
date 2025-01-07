package si.travelbuddy.travelbuddy.ui.trip

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TripUiState(
    val stops: List<String> = listOf("", "")
)

class TripViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TripUiState())
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    fun addStop() {
        _uiState.update { currentState ->
            currentState.copy(
                stops = currentState.stops + ""
            )
        }
    }

    fun updateStop(index: Int, text: String) {
        _uiState.update { currentState ->
            val oldStops = currentState.stops.toMutableList()
            oldStops[index] = text

            currentState.copy(
                stops = oldStops
            )
        }
    }

    fun resetStops() {
        _uiState.update { currentState ->
            currentState.copy(
                stops = listOf()
            )
        }
    }
}