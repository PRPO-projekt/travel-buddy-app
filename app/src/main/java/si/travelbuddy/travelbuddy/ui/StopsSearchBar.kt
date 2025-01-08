package si.travelbuddy.travelbuddy.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import si.travelbuddy.travelbuddy.model.Departures
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.ui.stops.StopsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    items: List<Stop>
) {
    DockedSearchBar(
        modifier = Modifier
            .padding(top = 8.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange
    ) {
        StopsList(items)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatefulStopsSearchBar(
    onFindStops: suspend (String) -> List<Stop>,
    onSearch: suspend (String) -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    var items by remember { mutableStateOf(listOf<Stop>()) }

    val coroutineScope = rememberCoroutineScope()

    StopsSearchBar(
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
                onSearch(it)
            }
        },
        active = active,
        onActiveChange = { active = it },
        items = items
    )
}

