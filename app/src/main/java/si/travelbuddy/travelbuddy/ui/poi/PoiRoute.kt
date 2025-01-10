package si.travelbuddy.travelbuddy.ui.poi

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun PoiRoute(
    viewModel: PoiViewModel = PoiViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column {
        PoiSearchBar(
            query = uiState.searchText,
            onQueryChange = { viewModel.updateSearch(it) },
            onSearch = {},
            active = uiState.searchActive,
            onActiveChange = { viewModel.updateActive(it) },
            items = uiState.items
        )
    }
}