package com.example.thesisapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.thesisapp.data.detection_model.ModelApiService
import com.example.thesisapp.data.detection_model.ModelRepository
import com.example.thesisapp.data.detection_model.NewsRequest
import com.example.thesisapp.data.detection_model.PredictionResponse
import com.example.thesisapp.data.news.LocalNewsRepository
import com.example.thesisapp.data.news.News
import com.example.thesisapp.data.news.NewsDao
import com.example.thesisapp.data.news.NewsDatabase
import com.example.thesisapp.data.news.NewsRepository
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel
import com.example.thesisapp.ui.viewmodel.DetectionUiState
import com.example.thesisapp.ui.viewmodel.NewsUiState
import io.mockk.coEvery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ViewModelDbIntegrationTest {

    private lateinit var newsDao: NewsDao
    private lateinit var db: NewsDatabase
    private lateinit var localNewsRepository: NewsRepository
    private lateinit var modelApiService: ModelApiService
    private lateinit var modelRepository: ModelRepository
    private lateinit var viewModel: ApplicationViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NewsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        newsDao = db.newsDao()

        modelApiService = mockModelApiService("real")

        localNewsRepository = LocalNewsRepository(newsDao)
        modelRepository = ModelRepository(modelApiService)

        viewModel = ApplicationViewModel(localNewsRepository, modelRepository)
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun addNewsUpdatesUiStateWithRealDbPersistence() = runTest(testDispatcher) {
        val news = News(
            id = 0,
            content = "New article from real DB",
            checkDate = "2024-06-08",
            isFake = false
        )

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())

            viewModel.addNews(news)
            advanceUntilIdle()

            val updatedUiState = awaitItem()
            assertEquals(1, updatedUiState.allNews?.size)
            assertEquals(news.content, updatedUiState.allNews?.first()?.content)
            assertTrue(updatedUiState.allNews?.first()?.id != 0)

            cancelAndConsumeRemainingEvents()
        }

        newsDao.getNews().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(news.content, items.first().content)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun deleteNewsRemovesItemFromRealDbAndUpdatesUiState() = runTest(testDispatcher) {
        val news1 =
            News(id = 0, content = "Item to be deleted", checkDate = "2024-06-08", isFake = false)
        val news2 =
            News(id = 0, content = "Item to remain", checkDate = "2024-06-09", isFake = false)

        newsDao.insert(news1)
        newsDao.insert(news2)
        advanceUntilIdle()

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())

            val populatedItems = awaitItem()
            assertEquals(2, populatedItems.allNews?.size)

            populatedItems.allNews?.any { it.content == news1.content }?.let { assertTrue(it) }
            populatedItems.allNews?.any { it.content == news2.content }?.let { assertTrue(it) }

            viewModel.deleteNews(1)
            advanceUntilIdle()

            val updatedUiState = awaitItem()
            assertEquals(1, updatedUiState.allNews?.size)
            assertEquals(news2.content, updatedUiState.allNews?.first()?.content)

            cancelAndConsumeRemainingEvents()
        }

        newsDao.getNews().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(news2.content, items.first().content)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun searchNewsFiltersWithRealDbAndUpdatesUiState() = runTest(testDispatcher) {
        val news1 = News(
            id = 0,
            content = "Article about Kotlin programming.",
            checkDate = "2024-01-01",
            isFake = false
        )
        val news2 = News(
            id = 0,
            content = "Deep dive into Jetpack Compose.",
            checkDate = "2024-01-02",
            isFake = false
        )
        val news3 = News(
            id = 0,
            content = "Latest news on Android development.",
            checkDate = "2024-01-03",
            isFake = false
        )

        newsDao.insert(news1)
        newsDao.insert(news2)
        newsDao.insert(news3)
        advanceUntilIdle()

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())
            assertEquals(3, awaitItem().allNews?.size)

            viewModel.searchNews("Compose")
            advanceUntilIdle()

            val filteredUiState = awaitItem()
            assertEquals(1, filteredUiState.allNews?.size)
            assertEquals(news2.content, filteredUiState.allNews?.first()?.content)
            assertEquals("Compose", viewModel.searchQuery.first())

            viewModel.searchNews("")
            advanceUntilIdle()
            val allItemsUiState = awaitItem()
            assertEquals(3, allItemsUiState.allNews?.size)
            assertEquals("", viewModel.searchQuery.first())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun checkNewsCallsModelRepositoryAndUpdatesUiState() = runTest(testDispatcher) {
        val contentToCheck = "This is a news article to check for fakery."

        coEvery { modelApiService.getDetection(NewsRequest(contentToCheck)) } returns PredictionResponse(
            "fake"
        )

        assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

        viewModel.checkNews(contentToCheck)
        assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

        advanceUntilIdle()

        assertTrue(viewModel.detectionUiState is DetectionUiState.Success)
        assertEquals(true, (viewModel.detectionUiState as DetectionUiState.Success).detectionIsFake)
    }

    @Test
    fun checkNews_shows_error_on_model_repository_exception() = runTest(testDispatcher) {
        val contentWithError = "Content that causes detection error."
        coEvery { modelApiService.getDetection(NewsRequest(contentWithError)) } throws RuntimeException(
            "Network issue"
        )

        assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

        viewModel.checkNews(contentWithError)
        assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

        advanceUntilIdle()

        assertTrue(viewModel.detectionUiState is DetectionUiState.Error)
    }
}