package com.example.thesisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thesisapp.ui.screens.HistoryDestination
import com.example.thesisapp.ui.screens.HomeDestination
import com.example.thesisapp.ui.components.BottomBar
import com.example.thesisapp.ui.components.TopBar
import com.example.thesisapp.ui.theme.ThesisAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThesisAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Scaffold(
                    topBar = {
                        TopBar(
                            title = if (currentRoute == HomeDestination.route) HomeDestination.titleRes else HistoryDestination.titleRes,
                        )
                    },
                    bottomBar = {
                        BottomBar(
                            fromHome = currentRoute == HomeDestination.route,
                            fromHistory = currentRoute == HistoryDestination.route,
                            navigateToHome = {
                                navController.navigate(HomeDestination.route) {
                                    popUpTo(HomeDestination.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            navigateToHistory = {
                                navController.navigate(HistoryDestination.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    ThesisApp(
                        navHostController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}