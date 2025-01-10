package si.travelbuddy.travelbuddy.ui.poi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import si.travelbuddy.travelbuddy.model.Poi

data class PoiUiState(
    val searchText: String = "",
    val items: List<Poi> = listOf(),
    val searchActive: Boolean = false,

    val currentItem: Poi? = null
)

class PoiViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PoiUiState())
    val uiState: StateFlow<PoiUiState> = _uiState.asStateFlow()

    fun updateActive(state: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                searchActive = state
            )
        }
    }

    fun updateSearch(text: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchText = ""
            )
        }
    }

    fun updateItems(items: List<Poi>) {
        _uiState.update { currentState ->
            currentState.copy(
                items = items
            )
        }
    }

    fun updateItem(item: Poi?) {
        _uiState.update { currentState ->
            currentState.copy(
                currentItem = item
            )
        }
    }
}