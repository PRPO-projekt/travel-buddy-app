package si.travelbuddy.travelbuddy.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.travelbuddy.travelbuddy.model.Poi

@Composable
fun PoiItem(
    item: Poi
) {
    Text(
        text = item.name
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoiSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    items: List<Poi>
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
        items.forEach {
            PoiItem(it)
        }
    }
}