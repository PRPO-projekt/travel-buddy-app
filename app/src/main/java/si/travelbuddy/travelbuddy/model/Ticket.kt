package si.travelbuddy.travelbuddy.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val arrival: String,
    val departure: String,
    val from: String,
    val id: String,
    val price: Double,
    val routeId: String,
    val stopTime: String,
    val to: String
)
