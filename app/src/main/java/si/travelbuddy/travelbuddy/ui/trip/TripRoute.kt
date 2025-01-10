package si.travelbuddy.travelbuddy.ui.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.RouteInfo
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.ui.stops.StopsSearchBar

@Composable
fun TripRoute(
    onFindStops: suspend (String) -> List<Stop>,
    onRoute: suspend (String, String, List<String>) -> RouteInfo?,
    viewModel: TripViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        uiState.stops.forEachIndexed { index, s ->
            Row {
                StopsSearchBar(
                    query = s.query,
                    onQueryChange = {
                        viewModel.updateStop(index, it)
                        coroutineScope.launch {
                            val items = onFindStops("$it.*")
                            viewModel.updateItems(index, items)
                        }
                    },
                    onSearch = {
                        val stopNames = uiState.stops.map { it.query }

                        if (stopNames[index].isNotEmpty()) {
                            viewModel.onSearch(index)
                        }
                        else {
                            return@StopsSearchBar
                        }

                        coroutineScope.launch {
                            val stops = stopNames
                                .asFlow()
                                .map { onFindStops(it) }
                                .map { it.firstOrNull()?.id ?: "" }
                                .toList()

                            if (stops.any { it.isEmpty() }) {
                                return@launch
                            }

                            val firstStop = stops.first()
                            val lastStop = stops.last()
                            val intermediateStops = stops.drop(1).dropLast(1)

                            viewModel.setLoadingRoute(true)
                            viewModel.setRouteInfo(onRoute(firstStop, lastStop, intermediateStops))
                            viewModel.setLoadingRoute(false)
                        }
                    },
                    active = s.active,
                    onActiveChange = { viewModel.setActive(index, it) },
                    items = s.items
                )

                if (index > 1) {
                    Button(
                        onClick = { viewModel.removeStop(index) },
                        contentPadding = PaddingValues(1.dp),
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove stop",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

        }

        Button(
            onClick = { viewModel.addStop() }
        ) {
            Text("Add stop")
        }

        if (uiState.loadingRoute) {
            Column {
                val loadingMsg = uiState.stops.joinToString(" → ") { it.query }
                Text("Searching route on path $loadingMsg")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }
        }

        if (uiState.route != null) {
            Text(text = uiState.route!!.duration.inWholeSeconds.toString())
        }
    }
}
