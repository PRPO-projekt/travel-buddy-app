package si.travelbuddy.travelbuddy.ui.ticket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import si.travelbuddy.travelbuddy.model.Departure
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.model.Trip

@Composable
fun TicketRoute(
    stop: Stop,
    departure: Departure
) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop")
            TextField(
                value = stop.name,
                onValueChange = {},
                enabled = false
            )

            Text("Route")
            TextField(
                value = departure.route.longName ?: "",
                onValueChange = {},
                enabled = false
            )
        }

    }
}