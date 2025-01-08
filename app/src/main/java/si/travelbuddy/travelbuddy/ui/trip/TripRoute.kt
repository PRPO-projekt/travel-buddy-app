package si.travelbuddy.travelbuddy.ui.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.ui.StopsSearchBar

@Composable
fun TripRoute(
    onFindStops: suspend (String) -> List<Stop>,
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
                    onSearch = { viewModel.onSearch(index) },
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
                            Icons.Filled.Close, "Remove stop",
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

        Text(uiState.stops.joinToString { it.query })
    }
}
