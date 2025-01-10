package si.travelbuddy.travelbuddy.ui.poi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.Poi

@Composable
fun PoiRoute(
    onFindPois: suspend (String) -> List<Poi>,
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

                    viewModel.updateItem(pois[0])
                    viewModel.updateActive(false)
                }
            },
            active = uiState.searchActive,
            onActiveChange = { viewModel.updateActive(it) },
            items = uiState.items
        )

        val curItem = uiState.currentItem
        if (curItem != null) {
            PoiItem(item = curItem)
        }
    }
}