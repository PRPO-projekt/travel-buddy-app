package si.travelbuddy.travelbuddy.model

import kotlinx.serialization.Serializable

@Serializable
data class Stop(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double
)
