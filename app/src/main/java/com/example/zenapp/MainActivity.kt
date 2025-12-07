package com.example.zenapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zenapp.navigation.Routes
import com.example.zenapp.ui.appselection.AppSelectionScreen
import com.example.zenapp.ui.blocklist.BlocklistScreen
import com.example.zenapp.ui.blocklist.BlocklistViewModel
import com.example.zenapp.ui.components.SettingsIcon
import com.example.zenapp.ui.components.ShieldCheck
import com.example.zenapp.ui.settings.SettingsScreen
import com.example.zenapp.ui.theme.ZenAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenAppTheme {
                ZenAppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenAppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    
    val blocklistViewModel: BlocklistViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            when (currentDestination?.route) {
                Routes.BLOCKLIST -> {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Bloqueos",
                                fontSize = 24.sp,
                                color = Color(0xFFE1E1E1)
                            )
                        },
                        actions = {
                            IconButton(onClick = { 
                                navController.navigate(Routes.APP_SELECTION) 
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar apps",
                                    tint = Color(0xFFBB86FC)
                                )
                            }

                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Más opciones",
                                        tint = Color(0xFFBB86FC)
                                    )
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Compartir lista") },
                                        onClick = {
                                            expanded = false
                                            Toast.makeText(context, "Compartir con alguna app", Toast.LENGTH_SHORT).show()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Compartir"
                                            )
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (currentDestination?.route in listOf(Routes.BLOCKLIST, Routes.SETTINGS)) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.ShieldCheck,
                                contentDescription = "Bloqueos"
                            )
                        },
                        label = { Text("Bloqueos") },
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.BLOCKLIST } == true,
                        onClick = {
                            navController.navigate(Routes.BLOCKLIST) {
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
                                imageVector = Icons.Filled.SettingsIcon,
                                contentDescription = "Ajustes"
                            )
                        },
                        label = { Text("Ajustes") },
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.SETTINGS } == true,
                        onClick = {
                            navController.navigate(Routes.SETTINGS) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.BLOCKLIST,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.BLOCKLIST) {
                BlocklistScreen(
                    onNavigateToAppSelection = { 
                        navController.navigate(Routes.APP_SELECTION) 
                    },
                    viewModel = blocklistViewModel
                )
            }
            
            composable(Routes.APP_SELECTION) {
                val currentSelectedApps = blocklistViewModel.uiState.collectAsState().value.selectedApps
                AppSelectionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onConfirm = { selectedApps ->
                        blocklistViewModel.addSelectedApps(selectedApps)
                    },
                    preSelectedApps = currentSelectedApps
                )
            }
            
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}
