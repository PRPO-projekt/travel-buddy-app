package si.travelbuddy.travelbuddy.ui.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import si.travelbuddy.travelbuddy.model.Departures
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
        uiState.stops.forEachIndexed { index, _ ->
            StopsSearchBar(
                onFindStops = onFindStops,
                onSearch = {
                    viewModel.updateStop(index, it)
                }
            )
        }

        Row {
            Button(
                onClick = { viewModel.addStop() }
            ) {
                Text("Add stop")
            }
            Button(
                onClick = { viewModel.resetStops() }
            ) {
                Text("Reset")
            }
        }

        Text(uiState.stops.joinToString())
    }
}