package si.travelbuddy.travelbuddy.ui.stops

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import si.travelbuddy.travelbuddy.model.Departure
import si.travelbuddy.travelbuddy.model.Departures
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.model.Trip
import si.travelbuddy.travelbuddy.ui.StatefulStopsSearchBar
import si.travelbuddy.travelbuddy.ui.StopsSearchBar

@Composable
fun StopsList(items: List<Stop>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        items.forEach { item ->
            Text(
                text = item.name
            )
        }
    }
}

@Composable
fun StopView(stop: Stop?, deps: List<Departure>, loaded: Boolean, onPurchaseTicket: (Stop, Departure) -> Unit) {
    if (stop == null) {
        Text("Search for a stop")
    } else {
        Column(Modifier.fillMaxWidth()) {
            Text("Name: ${stop.name}")
            Text("ID: ${stop.id}")
            DeparturesView(
                stop = stop,
                deps = deps,
                loaded = loaded,
                onPurchaseTicket = onPurchaseTicket
            )
        }
    }
}

@Composable
fun DeparturesView(stop: Stop, deps: List<Departure>, loaded: Boolean, onPurchaseTicket: (Stop, Departure) -> Unit) {
    if (!loaded) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            return
        }

    }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        deps.forEach {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    "${it.depTime}, ${it.route.longName}"
                )

                Button(
                    onClick = { onPurchaseTicket(stop, it) }
                ) {
                    Text("Purchase ticket")
                }
            }
        }
    }
}

@Composable
fun StopsRoute(
    onFindStops: suspend (String) -> List<Stop>,
    onFindStopDepartures: suspend (String) -> Departures,
    onPurchaseTicket: (Stop, Departure) -> Unit,
    viewModel: StopsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize()) {
        StatefulStopsSearchBar(
            onFindStops = onFindStops,
            onSearch = {
                val stops = onFindStops(it)

                if (stops.isEmpty()) {
                    viewModel.updateStop(null)
                    return@StatefulStopsSearchBar
                }

                viewModel.updateStop(stops[0])

                val deps = onFindStopDepartures(stops[0].id).departures
                viewModel.updateStopDepartures(deps)
            }
        )
        StopView(
            uiState.currentStop,
            uiState.currentStopDepartures,
            uiState.loadedDepartures,
            onPurchaseTicket = onPurchaseTicket
            )
    }
}