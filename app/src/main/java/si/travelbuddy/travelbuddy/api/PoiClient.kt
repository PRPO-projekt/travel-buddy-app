package si.travelbuddy.travelbuddy.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.io.IOException
import si.travelbuddy.travelbuddy.model.Poi

class PoiClient(_httpClient: HttpClient) {
    private val endpoint = "http://4.245.99.219:8090"

    private val httpClient = _httpClient

    suspend fun getPois(name: String): List<Poi> {
        try {
            val pois: List<Poi> = httpClient.get("$endpoint/pois") {
                url {
                    parameters.append("name", name)
                }
            }.body()

            return pois
        } catch (ex: IOException) {
            return listOf()
        }
    }

    suspend fun createPoi(poi: Poi) {
        val currentPois = getPois(".*")

        val count = currentPois.count()

        val input = poi.copy(
            id = count
        )

        httpClient.post("$endpoint/pois") {
            contentType(ContentType.Application.Json)
            setBody(input)
        }
    }
}