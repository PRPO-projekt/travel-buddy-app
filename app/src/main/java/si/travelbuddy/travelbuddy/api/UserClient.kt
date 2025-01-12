package si.travelbuddy.travelbuddy.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import si.travelbuddy.travelbuddy.model.User

@Serializable
data class LoginInput(
    val username: String,
    val password: String
)

class UserClient(_httpClient: HttpClient) {
    private val apiEndpoint = "http://4.245.99.219:8091"

    private val httpClient = _httpClient

    private var currentUser: User? = null

    suspend fun login(username: String, password: String): User? {
        val input = LoginInput(username, password)

        try {
            val user: User = httpClient.post("$apiEndpoint/login") {
                contentType(ContentType.Application.Json)
                setBody(input)
            }.body()

            currentUser = user

            return user
        } catch (ex: Exception) {
            return null;
        }
    }
}