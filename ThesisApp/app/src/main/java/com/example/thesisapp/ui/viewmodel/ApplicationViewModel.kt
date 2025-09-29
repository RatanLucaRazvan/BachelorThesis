package com.example.thesisapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesisapp.data.detection_model.ModelRepository
import com.example.thesisapp.data.detection_model.NewsRequest
import com.example.thesisapp.data.news.News
import com.example.thesisapp.data.news.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NewsUiState(
    val allNews: List<News>? = listOf(),
)

sealed interface DetectionUiState {
    data class Success(val detectionIsFake: Boolean) : DetectionUiState
    data object Error : DetectionUiState
    data object Loading : DetectionUiState
}

class ApplicationViewModel(
    private val localNewsRepository: NewsRepository,
    private val modelRepository: ModelRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val newsUiState: StateFlow<NewsUiState> =
            _searchQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        localNewsRepository.getNewsStream()
                    } else {
                        localNewsRepository.searchNewsStream(query)
                    }
                }
                .map { newsList ->
                    NewsUiState(allNews = newsList)
                }
                .catch { _ ->
                    emit(NewsUiState(allNews = null))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000L),
                    initialValue = NewsUiState()
                )


    var detectionUiState: DetectionUiState by mutableStateOf(DetectionUiState.Loading)
        private set

    fun checkNews(content: String) {
        val newsObject = NewsRequest(content)
        viewModelScope.launch {
            detectionUiState = DetectionUiState.Loading
            try {
                val detectionResponse = modelRepository.getDetection(newsObject)
                detectionUiState = DetectionUiState.Success(detectionResponse.prediction == "fake")
            } catch (e: Exception) {
                detectionUiState = DetectionUiState.Error
            }
        }
    }

    fun addNews(newItem: News) {
        viewModelScope.launch {
            localNewsRepository.insert(newItem)
        }
    }

    fun deleteNews(newsId: Int?) {
        if (newsId != null) {
            viewModelScope.launch {
                localNewsRepository.deleteNews(newsId)
            }
        }
    }

    fun searchNews(query: String) {
        _searchQuery.value = query
    }
}