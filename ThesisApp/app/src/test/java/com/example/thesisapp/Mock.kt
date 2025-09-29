package com.example.thesisapp

import com.example.thesisapp.data.detection_model.ModelApiService
import com.example.thesisapp.data.news.News
import com.example.thesisapp.data.news.NewsDao
import com.example.thesisapp.data.detection_model.PredictionResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf


fun mockNewsDao(): Pair<NewsDao, MutableStateFlow<List<News>>> {
    val mockDao = mockk<NewsDao>()
    val newsInDao = MutableStateFlow<List<News>>(emptyList())

    every { mockDao.getNews() } returns newsInDao.asStateFlow()

    coEvery { mockDao.insert(any()) } answers {
        val news = it.invocation.args[0] as News
        newsInDao.value += news
    }

    coEvery { mockDao.delete(any()) } answers {
        val newsId = it.invocation.args[0] as Int
        newsInDao.value = newsInDao.value.filter { news -> news.id != newsId }
    }

    every { mockDao.searchNews(any()) } answers {
        val query = it.invocation.args[0] as String
        flowOf(newsInDao.value.filter { news ->
            news.content.contains(query, ignoreCase = true)
        })
    }

    return Pair(mockDao, newsInDao)
}

fun mockModelApiService(prediction: String): ModelApiService {
    val mockService = mockk<ModelApiService>()
    coEvery { mockService.getDetection(any()) } returns PredictionResponse(prediction)
    return mockService
}
