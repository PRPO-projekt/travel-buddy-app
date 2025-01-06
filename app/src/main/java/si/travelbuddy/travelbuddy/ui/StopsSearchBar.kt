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
    onFindStops: suspend (String) -> List<Stop>,
    onSearch: suspend (String) -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    var items by remember { mutableStateOf(listOf<Stop>()) }

    val coroutineScope = rememberCoroutineScope()

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
                onSearch(it)
            }

        },
        active = active,
        onActiveChange = { active = it }
    ) {
        StopsList(items)
    }
}