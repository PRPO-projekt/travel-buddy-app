package si.travelbuddy.travelbuddy.ui.ticket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.model.Trip

@Composable
fun TicketRoute(
    stopId: String,
    tripId: String
) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop")
            TextField(
                value = stopId,
                onValueChange = {},
                enabled = false
            )

            Text("Trip")
            TextField(
                value = tripId,
                onValueChange = {},
                enabled = false
            )
        }

    }
}