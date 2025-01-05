package si.travelbuddy.travelbuddy.model

import kotlinx.serialization.Serializable

@Serializable
data class Departure(
    val uuid: String,
    val depTime: String,
    val trip: Trip,
    val route: Route
)

@Serializable
data class Departures(
    val stop: Stop,
    val departures: List<Departure>
)
