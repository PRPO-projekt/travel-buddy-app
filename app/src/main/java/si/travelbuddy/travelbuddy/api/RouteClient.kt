package si.travelbuddy.travelbuddy.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import si.travelbuddy.travelbuddy.model.RouteInfo
import java.io.IOException
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class RouteClient(_httpClient: HttpClient) {
    private val endpoint = "http://4.245.99.219:8081"

    private val httpClient = _httpClient

    suspend fun getRoute(fromId: String, toId: String, intermediateStops: List<String> = listOf()): RouteInfo? {
        try {
            val inter = intermediateStops.joinToString(";")
            val s: String = httpClient.get("$endpoint/route") {
                url {
                    parameters.append("fromId", fromId)
                    parameters.append("toId", toId)
                    parameters.append("depTime", LocalDateTime.now().plusMinutes(10).toString())
                    parameters.append("intermediateStops", inter)
                }
            }.body()

            val obj = Json.parseToJsonElement(s)

            val duration = obj.jsonObject["Duration"]?.jsonPrimitive?.intOrNull

            if (duration != null) {
                return RouteInfo(
                    duration = duration.seconds
                )
            } else {
                return null
            }
        } catch (ex: IOException) {
            return null
        }
    }
}