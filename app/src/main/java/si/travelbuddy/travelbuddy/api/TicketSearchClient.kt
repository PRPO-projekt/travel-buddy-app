package si.travelbuddy.travelbuddy.api

import io.ktor.client.HttpClient
import java.util.UUID

class TicketSearchClient(_httpClient: HttpClient) {
    private val apiEndpoint = "";

    private val httpClient = _httpClient

    suspend fun getDepartureTickets(depId: UUID) {
        TODO()
    }

}