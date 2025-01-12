package si.travelbuddy.travelbuddy.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Leg(
    val Mode: String,
    val Duration: Long,
    val EndTime: Long,
    val StartTime: Long
)

@Serializable
data class RouteInfo(
    val StartTime: Long,
    val EndTime: Long,
    val TotalDuration: Long,
    val TotalDistance: Double,

    val Legs: List<Leg>
)
