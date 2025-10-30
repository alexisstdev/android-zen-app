package com.example.zenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zenapp.ui.blocklist.BlocklistScreen
import com.example.zenapp.ui.settings.SettingsScreen
import com.example.zenapp.ui.theme.ZenAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_shield_check),
                            contentDescription = "Bloqueos"
                        )
                    },
                    label = { Text("Bloqueos") },
                    selected = currentDestination?.hierarchy?.any { it.route == "blocklist" } == true,
                    onClick = {
                        navController.navigate("blocklist") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
                            contentDescription = "Ajustes"
                        )
                    },
                    label = { Text("Ajustes") },
                    selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
                    onClick = {
                        navController.navigate("settings") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "blocklist",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("blocklist") {
                BlocklistScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}

