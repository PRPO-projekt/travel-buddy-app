package si.travelbuddy.travelbuddy.ui.trip

import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import si.travelbuddy.travelbuddy.model.RouteInfo
import si.travelbuddy.travelbuddy.model.Stop

data class TripSearchState(
    val query: String = "",
    val active: Boolean = false,
    val items: List<Stop> = listOf()
)

data class TripUiState(
    val stops: List<TripSearchState> = listOf(TripSearchState(), TripSearchState()),
    val loadingRoute: Boolean = false,
    val route: RouteInfo? = null
)

class TripViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TripUiState())
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    fun addStop() {
        _uiState.update { currentState ->
            currentState.copy(
                stops = currentState.stops + TripSearchState()
            )
        }
    }

    fun updateStop(index: Int, text: String) {
        _uiState.update { currentState ->
            val oldStops = currentState.stops.toMutableList()
            oldStops[index] = oldStops[index].copy(
                query = text
            )

            currentState.copy(
                stops = oldStops
            )
        }
    }

    fun updateItems(index: Int, items: List<Stop>) {
        _uiState.update { currentState ->
            val oldStops = currentState.stops.toMutableList()
            oldStops[index] = oldStops[index].copy(
                items = items
            )

            currentState.copy(
                stops = oldStops
            )
        }
    }

    fun setActive(index: Int, state: Boolean) {
        _uiState.update { currentState ->
            val l = currentState.stops.mapIndexed { idx, tripSearchState ->
                tripSearchState.copy(
                    active = state && idx == index
                )
            }

            currentState.copy(
                stops = l
            )
        }
    }

    fun onSearch(index: Int) {
        _uiState.update { currentState ->
            val oldStops = currentState.stops.toMutableList()
            oldStops[index] = oldStops[index].copy(
                active = false
            )

            if (index < oldStops.count() - 1) {
                oldStops[index + 1] = oldStops[index + 1].copy(
                    active = true
                )
            }

            currentState.copy(
                stops = oldStops
            )
        }
    }

    fun removeStop(index: Int) {
        _uiState.update { currentState ->
            val oldStops = currentState.stops.toMutableList()
            oldStops.removeAt(index)

            currentState.copy(
                stops = oldStops
            )
        }
    }

    fun setLoadingRoute(state: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                loadingRoute = state
            )
        }
    }

    fun setRouteInfo(state: RouteInfo?) {
        _uiState.update { currentState ->
            currentState.copy(
                route = state
            )
        }
    }
}