package si.travelbuddy.travelbuddy.ui.stops

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import si.travelbuddy.travelbuddy.model.*
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
fun StopView(stop: Stop?, deps: List<Departure>) {
    if (stop == null) {
        Text("Search for a stop")
    } else {
        Column(Modifier.fillMaxWidth()) {
            Text("Name: ${stop.name}")
            Text("ID: ${stop.id}")
            DeparturesView(deps)
        }
    }
}

@Composable
fun DeparturesView(deps: List<Departure>) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        deps.forEach {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    "${it.depTime}, ${it.route.longName}"
                )
            }
        }
    }
}

@Composable
fun StopsRoute(
    onFindStops: suspend (String) -> List<Stop>,
    onFindStopDepartures: suspend (String) -> Departures
) {
    var currentStop by remember { mutableStateOf<Stop?>(null) }
    var currentStopDepartures by remember { mutableStateOf(listOf<Departure>()) }

    Column(Modifier.fillMaxSize()) {
        StopsSearchBar(
            onFindStops = onFindStops,
            onSearch = {
                val stops = onFindStops(it)

                if (stops.isEmpty()) {
                    currentStop = null;
                    return@StopsSearchBar
                }

                currentStop = stops[0];

                currentStopDepartures = onFindStopDepartures(stops[0].id).departures
            }
        )
        StopView(currentStop, currentStopDepartures)
    }
}