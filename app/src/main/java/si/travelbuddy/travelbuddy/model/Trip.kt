package si.travelbuddy.travelbuddy.model

import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String,
    val routeId: String,
    val tripHeadsign: String?,
    val blockId: Int?
)
