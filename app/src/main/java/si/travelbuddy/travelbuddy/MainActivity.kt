package si.travelbuddy.travelbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import si.travelbuddy.travelbuddy.api.TimetableClient
import si.travelbuddy.travelbuddy.ui.stops.StopsRoute
import si.travelbuddy.travelbuddy.ui.stops.StopsViewModel
import si.travelbuddy.travelbuddy.ui.theme.TravelBuddyTheme
import si.travelbuddy.travelbuddy.ui.ticket.TicketRoute

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                                viewModel = StopsViewModel(),
                                onPurchaseTicket = { trip, stop ->
                                    navController.navigate(Ticket(trip, stop))
                                }
                            )
                        }
                        composable<Trip> {
                            Greeting("Linux")
                        }
                        composable<Ticket> {
                            val ticket: Ticket = it.toRoute()
                            TicketRoute(ticket.stop, ticket.trip)
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

@kotlinx.serialization.Serializable
object Stops

@kotlinx.serialization.Serializable
object Trip

@kotlinx.serialization.Serializable
data class Ticket(val stop: String,
                  val trip: String)

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        TopLevelRoute(
            name = "Stops",
            route = Stops,
            icon = Icons.Default.Menu
        ),
        TopLevelRoute(
            name = "Trip",
            route = Trip,
            icon = Icons.Default.Place
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

