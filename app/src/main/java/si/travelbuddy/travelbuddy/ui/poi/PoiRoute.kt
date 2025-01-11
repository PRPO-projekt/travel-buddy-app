package si.travelbuddy.travelbuddy.ui.poi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.Poi
import si.travelbuddy.travelbuddy.model.Stop

@Composable
fun PoiRoute(
    onFindPois: suspend (String) -> List<Poi>,
    getStop: suspend (String) -> Stop?,
    onShowMap: (Double?, Double?) -> Unit,
    viewModel: PoiViewModel = PoiViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        PoiSearchBar(
            query = uiState.searchText,
            onQueryChange = {
                viewModel.updateSearch(it)

                if (it.isNotEmpty()) {
                    coroutineScope.launch {
                        viewModel.updateItems(onFindPois("$it.*"))
                    }
                }
            },
            onSearch = {
                coroutineScope.launch {
                    val pois = onFindPois(it)

                    if (pois.isEmpty()) {
                        viewModel.updateItem(null)
                        return@launch
                    }

                    val poi = pois[0]
                    viewModel.updateItem(poi)
                    viewModel.updateActive(false)

                    val stop = getStop(poi.idPostaje.toString())
                    if (stop != null) {
                        viewModel.updateNearestStop(stop.name)
                    }
                    else {
                        viewModel.updateNearestStop("")
                    }
                }
            },
            active = uiState.searchActive,
            onActiveChange = { viewModel.updateActive(it) },
            items = uiState.items
        )

        val curItem = uiState.currentItem
        if (curItem != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(onClick = { onShowMap(curItem.lat, curItem.lon) }) {
                    Text("Show on map")
                }
                PoiItem(
                    item = curItem,
                    stopName = uiState.nearestStopName
                )
            }
        }
    }
}