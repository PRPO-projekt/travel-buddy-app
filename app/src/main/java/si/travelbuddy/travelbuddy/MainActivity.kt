package si.travelbuddy.travelbuddy

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.CircleLayer
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.compose.source.getBaseSource
import dev.sargunv.maplibrecompose.core.CameraPosition
import io.github.dellisd.spatialk.geojson.Position
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import si.travelbuddy.travelbuddy.api.PoiClient
import si.travelbuddy.travelbuddy.api.PurchaseClient
import si.travelbuddy.travelbuddy.api.RouteClient
import si.travelbuddy.travelbuddy.api.TicketSearchClient
import si.travelbuddy.travelbuddy.api.TimetableClient
import si.travelbuddy.travelbuddy.api.UserClient
import si.travelbuddy.travelbuddy.model.Departure
import si.travelbuddy.travelbuddy.model.Stop
import si.travelbuddy.travelbuddy.ui.poi.PoiRoute
import si.travelbuddy.travelbuddy.ui.poi.PoiViewModel
import si.travelbuddy.travelbuddy.ui.stops.StopsRoute
import si.travelbuddy.travelbuddy.ui.stops.StopsViewModel
import si.travelbuddy.travelbuddy.ui.theme.TravelBuddyTheme
import si.travelbuddy.travelbuddy.ui.ticket.TicketRoute
import si.travelbuddy.travelbuddy.ui.ticket.TicketViewModel
import si.travelbuddy.travelbuddy.ui.trip.TripRoute
import si.travelbuddy.travelbuddy.ui.trip.TripViewModel
import si.travelbuddy.travelbuddy.ui.user.UserRoute
import si.travelbuddy.travelbuddy.ui.user.UserViewModel
import java.util.UUID
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    private val httpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            }
        }
    }

    private val timetableClient by lazy {
        TimetableClient(httpClient)
    }

    private val routeClient by lazy {
        RouteClient(httpClient)
    }

    private val poiClient by lazy {
        PoiClient(httpClient)
    }

    private val userClient by lazy {
        UserClient(httpClient)
    }

    private val ticketSearchClient by lazy {
        TicketSearchClient(httpClient)
    }

    private val purchaseClient by lazy {
        PurchaseClient(httpClient)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stopsViewModel: StopsViewModel by viewModels()
        val tripViewModel: TripViewModel by viewModels()
        val poiViewModel: PoiViewModel by viewModels()
        val userViewModel: UserViewModel by viewModels()
        val ticketViewModel: TicketViewModel by viewModels()

        enableEdgeToEdge()

        setContent {
            TravelBuddyTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Stops,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Stops> {
                            StopsRoute(
                                onFindStops = { timetableClient.getStopResults(it) },
                                onFindStopDepartures = { timetableClient.getStopDepartures(it) },
                                viewModel = stopsViewModel,
                                onPurchaseTicket = { trip, stop ->
                                    navController.navigate(Ticket(trip, stop))
                                }
                            )
                        }
                        composable<POI> {
                            PoiRoute(
                                onFindPois = { poiClient.getPois(it) },
                                getStop = { timetableClient.getStop(it) },
                                onShowMap = { lat, lon ->
                                    navController.navigate(MapNav(lat, lon)) },
                                viewModel = poiViewModel
                            )
                        }
                        composable<Trip> {
                            TripRoute(
                                onFindStops = { timetableClient.getStopResults(it) },
                                onRoute = { fromId, toId, inter ->
                                    routeClient.getRoute(fromId, toId, inter)
                                          },
                                viewModel = tripViewModel
                            )
                        }
                        composable<User> {
                            UserRoute(
                                onLogin = { username, password ->
                                    userClient.login(username, password)
                                },
                                viewModel = userViewModel
                            )
                        }
                        composable<Ticket>(
                            typeMap = mapOf(
                                typeOf<Ticket>() to navTypeOf<Ticket>(),
                                typeOf<Stop>() to navTypeOf<Stop>(),
                                typeOf<Departure>() to navTypeOf<Departure>()
                            )
                        ) {
                            val ticket: Ticket = it.toRoute()
                            TicketRoute(
                                ticket.stop,
                                ticket.trip,
                                onGetTickets = {
                                    val tickets = ticketSearchClient.ticketSearch()
                                    tickets
                                },
                                onPurchase = { ticketId ->
                                    val uuid = UUID.randomUUID()
                                    val userUuid = UUID(0, 0)
                                    val ticketUuid = UUID(0, 0)

                                    purchaseClient.createTransaction(uuid, ticketUuid, userUuid)

                                    uuid.toString()
                                },
                                onConfirmPurchase = {},
                                viewModel = ticketViewModel)
                        }
                        composable<MapNav>(
                            typeMap = mapOf(
                                typeOf<MapNav>() to navTypeOf<MapNav>()
                            )
                        ) {
                            val coords: MapNav = it.toRoute()
                            val camera = if (coords.lat == null || coords.lon == null) {
                                rememberCameraState()
                            } else {
                                rememberCameraState(
                                    firstPosition = CameraPosition(
                                        target = Position(
                                            latitude = coords.lat,
                                            longitude = coords.lon
                                        ),
                                        zoom = 16.0
                                    )
                                )
                            }
                            MaplibreMap(
                                styleUri = "https://tiles.openfreemap.org/styles/liberty",
                                cameraState = camera
                            ) {
                                val tiles = getBaseSource(id = "openmaptiles")
                                CircleLayer(id = "example", source = tiles)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TravelBuddyTheme {
        Greeting("Android")
    }
}

@Serializable
object Stops

@Serializable
object POI

@Serializable
object Trip

@Serializable
object User

@Serializable
data class Ticket(val stop: Stop,
                  val trip: Departure)

@Serializable
data class MapNav(val lat: Double? = null, val lon: Double? = null)

inline fun <reified T> navTypeOf(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(Uri.decode(value))

    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putString(key, json.encodeToString(value))

}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        TopLevelRoute(
            name = "Stops",
            route = Stops,
            icon = Icons.Default.DirectionsTransit
        ),
        TopLevelRoute(
            name = "POI",
            route = POI,
            icon = Icons.Default.Place
        ),
        TopLevelRoute(
            name = "Trip",
            route = Trip,
            icon = Icons.Default.Explore
        ),
        TopLevelRoute(
            name = "User",
            route = User,
            icon = Icons.Default.Person
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) }
            )
        }
    }
}

