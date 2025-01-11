package si.travelbuddy.travelbuddy.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val surname: String,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String,
    val created: String
)
