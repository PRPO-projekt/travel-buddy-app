package si.travelbuddy.travelbuddy.ui.ticket

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import si.travelbuddy.travelbuddy.model.Ticket

data class TicketUiState(
    val tickets: List<Ticket> = listOf()
)

class TicketViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    fun updateTickets(l: List<Ticket>) {
        _uiState.update { currentState ->
            currentState.copy(
                tickets = l
            )
        }
    }
}