package si.travelbuddy.travelbuddy.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.io.IOException
import si.travelbuddy.travelbuddy.model.Ticket
import java.util.UUID

class TicketSearchClient(_httpClient: HttpClient) {
    private val apiEndpoint = "http://4.245.99.219:8093";

    private val httpClient = _httpClient

    suspend fun ticketSearch(): List<Ticket> {
        try {
            val tickets: List<Ticket> = httpClient.get("$apiEndpoint/ticketSearch").body();

            return tickets
        } catch (ex: IOException) {
            return listOf()
        }
    }

}