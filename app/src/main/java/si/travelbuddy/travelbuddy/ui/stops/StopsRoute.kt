package si.travelbuddy.travelbuddy.ui.stops

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import si.travelbuddy.travelbuddy.model.*

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
            .fillMaxWidth()) {
        deps.forEach {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    "${it.depTime}, ${it.route.longName}"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsRoute(
    onFindStops: suspend (String) -> List<Stop>,
    onFindStopDepartures: suspend (String) -> Departures
    ) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    var items by remember { mutableStateOf(listOf<Stop>()) }

    var currentStop by remember { mutableStateOf<Stop?>(null) }
    var currentStopDepartures by remember { mutableStateOf(listOf<Departure>()) }

    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        DockedSearchBar(
            modifier = Modifier
                .padding(top = 8.dp),
            query = searchText,
            onQueryChange = {
                searchText = it
                if (searchText.isNotEmpty()) {
                    coroutineScope.launch {
                        items = onFindStops("$searchText.*")
                    }
                }
            },
            onSearch = {
                active = false

                coroutineScope.launch {
                    val item = onFindStops(it)

                    if (item.isEmpty()) {
                        currentStop = null;
                        return@launch
                    }

                    currentStop = item[0];

                    currentStopDepartures = onFindStopDepartures(item[0].id).departures
                }
            },
            active = active,
            onActiveChange = { active = it }
        ) {
            StopsList(items)
        }
        StopView(currentStop, currentStopDepartures)
    }
}