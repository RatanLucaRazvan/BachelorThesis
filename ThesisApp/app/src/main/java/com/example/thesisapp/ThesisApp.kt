package com.example.thesisapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel
import com.example.thesisapp.ui.viewmodel.ViewModelProvider
import com.example.thesisapp.ui.navigation.NewsNavHost


@Composable
fun ThesisApp(
    navHostController: NavHostController,
    viewModel: ApplicationViewModel = viewModel(factory = ViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    NewsNavHost(navController = navHostController, viewModel = viewModel, modifier = modifier)
}