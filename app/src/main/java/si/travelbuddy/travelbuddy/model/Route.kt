package si.travelbuddy.travelbuddy.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String,
    val shortName: String? = null,
    val longName: String? = null,
)
