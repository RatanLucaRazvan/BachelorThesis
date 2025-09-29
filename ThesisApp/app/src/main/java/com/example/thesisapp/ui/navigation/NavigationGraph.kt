package com.example.thesisapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel
import com.example.thesisapp.ui.screens.HistoryDestination
import com.example.thesisapp.ui.screens.HistoryScreen
import com.example.thesisapp.ui.screens.HomeDestination
import com.example.thesisapp.ui.screens.HomeScreen

@Composable
fun NewsNavHost(
    navController: NavHostController,
    viewModel: ApplicationViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                viewModel = viewModel,
            )
        }
        composable(route = HistoryDestination.route) {
            HistoryScreen(
                viewModel = viewModel,
            )
        }
    }
}