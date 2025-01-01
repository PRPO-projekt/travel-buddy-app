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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.Serializable

const val stopsApiEndpoint = "http://130.61.10.203:8080"

@Serializable
data class Stop(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double
)

private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

suspend fun getStopResults(stopName: String): List<Stop> {
    try {
        val stops: List<Stop> = client.get("$stopsApiEndpoint/stops") {
            url {
                parameters.append("name", stopName)
            }
        }.body()

        return stops
    } catch (ex: IOException) {
        return listOf()
    }
}

@Serializable
data class Trip(
    val id: String,
    val routeId: String,
    val tripHeadsign: String?,
    val blockId: Int?
)

@Serializable
data class Route(
    val id: String,
    val shortName: String? = null,
    val longName: String? = null,
)


@Serializable
data class Departure(
    val uuid: String,
    val depTime: String,
    val trip: Trip,
    val route: Route
)

@Serializable
data class Departures(
    val stop: Stop,
    val departures: List<Departure>
)

suspend fun getStopDepartures(stopId: String): Departures {
    val deps: Departures = client.get(stopsApiEndpoint) {
        url {
            appendPathSegments("stops", stopId, "departures")
        }
    }.body()

    return deps
}

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
fun StopsRoute() {
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
                        items = getStopResults("$searchText.*")
                    }
                }
            },
            onSearch = {
                active = false

                coroutineScope.launch {
                    val item = getStopResults(it)

                    if (item.isEmpty()) {
                        currentStop = null;
                        return@launch
                    }

                    currentStop = item[0];

                    currentStopDepartures = getStopDepartures(item[0].id).departures
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