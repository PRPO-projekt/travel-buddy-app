package si.travelbuddy.travelbuddy.ui.ticket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.Departure
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.model.Ticket

@Composable
fun TicketRoute(
    stop: Stop,
    departure: Departure,
    onGetTickets: suspend () -> List<Ticket>,
    onPurchase: suspend (String) -> String,
    onConfirmPurchase: suspend (String) -> Unit,
    viewModel: TicketViewModel = TicketViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text("Stop")
            TextField(
                value = stop.name,
                onValueChange = {},
                enabled = false
            )

            Text("Route")
            TextField(
                value = departure.route.longName ?: "",
                onValueChange = {},
                enabled = false
            )

            LaunchedEffect(departure) {
                coroutineScope.launch {
                    val tickets = onGetTickets()
                    viewModel.updateTickets(tickets)
                }
            }

            val tickets = uiState.tickets
            if (tickets.isEmpty()) {
                Text("Searching tickets...")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                tickets
                    .filter { it.stopTime == departure.uuid }
                    .forEach {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Found ticket with price: ${it.price}")
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val id = onPurchase(it.id)
                                        viewModel.updatePurchaseId(id)
                                    }

                                }
                            ) {
                                Text("Purchase ticket")
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        onConfirmPurchase(uiState.purchaseId)
                                    }
                                }
                            ) {
                                Text("Confirm purchase")
                            }
                        }
                    }
            }
        }
    }
}