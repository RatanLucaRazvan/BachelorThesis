package com.example.thesisapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.thesisapp.R
import com.example.thesisapp.ui.components.NewsItem
import com.example.thesisapp.ui.navigation.NavigationDestination
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel
import com.example.thesisapp.ui.viewmodel.NewsUiState


object HistoryDestination : NavigationDestination {
    override val route: String
        get() = "history"
    override val titleRes: Int
        get() = R.string.history_title

}

@Composable
fun HistoryScreen(
    viewModel: ApplicationViewModel,
    modifier: Modifier = Modifier
) {
    val newsUiState by viewModel.newsUiState.collectAsState()
    HistoryScreenContent(
        modifier = modifier.testTag("HistoryScreenContent"),
        newsUiState = newsUiState,
        viewModel = viewModel,
    )
}

@Composable
fun HistoryScreenContent(
    modifier: Modifier = Modifier,
    newsUiState: NewsUiState,
    viewModel: ApplicationViewModel,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.background_color)),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedTextField(
            modifier = Modifier
                .size(380.dp, 70.dp)
                .padding(top = 12.dp),
            value = searchQuery,
            onValueChange = { newSearch ->
                viewModel.searchNews(newSearch)
            },
            shape = RoundedCornerShape(20.dp),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = stringResource(R.string.search_icon_description),
                    tint = colorResource(R.color.black)
                )
            },
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedContainerColor = colorResource(R.color.white),
                focusedContainerColor = colorResource(R.color.white),
                unfocusedLabelColor = colorResource(R.color.black),
                focusedLabelColor = colorResource(R.color.black),
                focusedTextColor = colorResource(R.color.black),
                unfocusedTextColor = colorResource(R.color.black),
                focusedIndicatorColor = colorResource(R.color.black),
                unfocusedIndicatorColor = colorResource(R.color.black),
                cursorColor = colorResource(R.color.black)
            )
        )
        if (newsUiState.allNews != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                items(items = newsUiState.allNews, key = { news -> news.id }) { news ->
                    NewsItem(
                        news = news,
                        viewModel = viewModel,
                    )
                }

            }
        } else {
            Log.e("HomeScreen", "List retrieve error")
            Box(modifier = Modifier.size(600.dp)) {
                Text(
                    text = stringResource(R.string.retrieve_list_error),
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}