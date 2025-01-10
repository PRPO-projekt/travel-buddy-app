package si.travelbuddy.travelbuddy.model

data class Poi(
    val id: Int,
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double,
    val idPostaje: Int
)
