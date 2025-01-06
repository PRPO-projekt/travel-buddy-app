package si.travelbuddy.travelbuddy.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import si.travelbuddy.travelbuddy.model.*
import java.io.IOException

class TimetableClient(_httpClient: HttpClient) {
    private val apiEndpoint = "http://130.61.10.203:8080"

    private val httpClient = _httpClient

    suspend fun getStopResults(stopName: String): List<Stop> {
        try {
            val stops: List<Stop> = httpClient.get("$apiEndpoint/stops") {
                url {
                    parameters.append("name", stopName)
                }
            }.body()

            return stops
        } catch (ex: IOException) {
            return listOf()
        }
    }

    suspend fun getStopDepartures(stopId: String): Departures {
        val deps: Departures = httpClient.get(apiEndpoint) {
            url {
                appendPathSegments("stops", stopId, "departures")
            }
        }.body()

        return deps
    }
}