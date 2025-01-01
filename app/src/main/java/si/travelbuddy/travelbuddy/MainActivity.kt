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
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import si.travelbuddy.travelbuddy.ui.stops.StopsRoute
import si.travelbuddy.travelbuddy.ui.theme.TravelBuddyTheme

class MainActivity : ComponentActivity() {
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
                            StopsRoute()
                        }
                        composable<Trip> {
                            Greeting("Linux")
                        }
                        composable("ticket" +
                                "?tripId={tripId}" +
                                "&stopId={stopId}",
                            arguments = listOf(
                                navArgument("tripId") {
                                    type = NavType.StringType
                                },
                                navArgument("stopId") {
                                    type = NavType.StringType
                                }
                            )
                        ) {

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

