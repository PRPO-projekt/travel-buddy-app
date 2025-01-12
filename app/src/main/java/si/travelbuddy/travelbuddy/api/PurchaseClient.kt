package si.travelbuddy.travelbuddy.api

import androidx.compose.animation.core.rememberTransition
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PurchaseRequest(
    val racunId: String,
    val stanjeRacuna: Int,
    val ticketId: String,
    val userId: String
)

class PurchaseClient(_httpClient: HttpClient) {
    private val apiEndpoint = "http://4.245.99.219:8094";

    private val httpClient = _httpClient

    suspend fun createTransaction(uuid: UUID, ticketId: UUID, userId: UUID): String {
        try {
            val req = PurchaseRequest(
                racunId = uuid.toString(),
                stanjeRacuna = 0,
                ticketId = ticketId.toString(),
                userId = userId.toString()
            )

            httpClient.post("$apiEndpoint/ticketSearch") {
                contentType(ContentType.Application.Json)
                setBody(req)
            }

            return ticketId.toString()
        } catch (ex: IOException) {
            return ""
        }
    }

    suspend fun updateTransaction(ticketId: UUID) {
        try {
            val forTicket: List<PurchaseRequest> = httpClient.get("${apiEndpoint}/ticketSearch/tickets/${ticketId}").body()

            if (forTicket.isNotEmpty()) {
                val transaction = forTicket.first()

                httpClient.put("${apiEndpoint}/ticketSearch/${transaction.racunId}") {
                    contentType(ContentType.Application.Json)
                    setBody(transaction.copy(
                        stanjeRacuna = 1
                    ))
                }
            }
        } catch (ex: IOException) {
            return
        }
    }
}