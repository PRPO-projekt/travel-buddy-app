package si.travelbuddy.travelbuddy.ui.poi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import si.travelbuddy.travelbuddy.model.Poi
import si.travelbuddy.travelbuddy.ui.theme.TravelBuddyTheme

@Composable
fun PoiItem(
    item: Poi
) {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = item.name,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.description
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PoiItemPreview() {
    TravelBuddyTheme {
        PoiItem(Poi(
            id = 1,
            name = "Ljubljanski grad",
            description = "Ljubljanski grad je ljubljanska srednjeveška arhitekturna znamenitost," +
                    " ki stoji na severozahodnem delu Grajskega griča na nadmorski višini 376 m",
            lat = 46.04879,
            lon = 14.508546,
            idPostaje = 1122804
        ))
    }
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
            Text(it.name)
        }
    }
}