package com.example.thesisapp

import app.cash.turbine.test
import com.example.thesisapp.data.detection_model.ModelApiService
import com.example.thesisapp.data.detection_model.ModelRepository
import com.example.thesisapp.data.news.News
import com.example.thesisapp.data.news.NewsDao
import com.example.thesisapp.data.news.LocalNewsRepository
import com.example.thesisapp.data.news.NewsRepository
import com.example.thesisapp.data.detection_model.NewsRequest
import com.example.thesisapp.data.detection_model.PredictionResponse
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel
import com.example.thesisapp.ui.viewmodel.DetectionUiState
import com.example.thesisapp.ui.viewmodel.NewsUiState
import io.mockk.coEvery
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ApplicationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var newsDao: NewsDao
    private lateinit var newsInDaoFlow: MutableStateFlow<List<News>>
    private lateinit var localNewsRepository: NewsRepository
    private lateinit var modelApiService: ModelApiService
    private lateinit var modelRepository: ModelRepository
    private lateinit var viewModel: ApplicationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val (mockDao, internalFlow) = mockNewsDao()
        newsDao = mockDao
        newsInDaoFlow = internalFlow

        modelApiService = mockModelApiService("real")

        localNewsRepository = LocalNewsRepository(newsDao)
        modelRepository = ModelRepository(modelApiService)

        viewModel = ApplicationViewModel(localNewsRepository, modelRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `newsUiState is initialized with empty list`() = runTest(testDispatcher) {
        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `addNews adds a news item and updates ui state`() = runTest(testDispatcher) {
        val news1 = News(
            id = 1,
            content = "Breaking news about a new discovery.",
            checkDate = "2024-06-07",
            isFake = false
        )

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())

            viewModel.addNews(news1)
            advanceUntilIdle()

            val updatedUiState = awaitItem()
            assertEquals(1, updatedUiState.allNews?.size)
            assertEquals(news1, updatedUiState.allNews?.first())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `deleteNews removes a news item and updates ui state`() = runTest(testDispatcher) {
        val news1 = News(
            id = 1,
            content = "Old article about technology.",
            checkDate = "2023-01-01",
            isFake = false
        )
        val news2 = News(
            id = 2,
            content = "Another piece of news from yesterday.",
            checkDate = "2024-06-06",
            isFake = true
        )

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())

            newsInDaoFlow.value = listOf(news1, news2)
            advanceUntilIdle()

            assertEquals(NewsUiState(allNews = listOf(news1, news2)), awaitItem())

            viewModel.deleteNews(news1.id)
            advanceUntilIdle()

            val updatedUiState = awaitItem()
            assertEquals(1, updatedUiState.allNews?.size)
            assertEquals(news2, updatedUiState.allNews?.first())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `checkNews sets detectionUiState to success true for fake news prediction`() =
        runTest(testDispatcher) {
            val fakeContent =
                "This content is intentionally designed to be classified as fake news."

            coEvery { modelApiService.getDetection(NewsRequest(fakeContent)) } returns PredictionResponse(
                "fake"
            )

            viewModel.checkNews(fakeContent)
            assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

            advanceUntilIdle()

            assertTrue(viewModel.detectionUiState is DetectionUiState.Success)
            assertEquals(
                true,
                (viewModel.detectionUiState as DetectionUiState.Success).detectionIsFake
            )
        }

    @Test
    fun `checkNews sets detectionUiState to success false for real news prediction`() =
        runTest(testDispatcher) {
            val realContent = "This is a factual report on recent scientific findings."
            coEvery { modelApiService.getDetection(NewsRequest(realContent)) } returns PredictionResponse(
                "real"
            )

            viewModel.checkNews(realContent)
            assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

            advanceUntilIdle()

            assertTrue(viewModel.detectionUiState is DetectionUiState.Success)
            assertEquals(
                false,
                (viewModel.detectionUiState as DetectionUiState.Success).detectionIsFake
            )
        }

    @Test
    fun `checkNews sets detectionUiState to error on API exception`() = runTest(testDispatcher) {
        val contentWithError = "Content that will cause an error during detection."
        coEvery { modelApiService.getDetection(NewsRequest(contentWithError)) } throws RuntimeException(
            "API connectivity issue"
        )

        viewModel.checkNews(contentWithError)
        assertEquals(DetectionUiState.Loading, viewModel.detectionUiState)

        advanceUntilIdle()

        assertTrue(viewModel.detectionUiState is DetectionUiState.Error)
    }

    @Test
    fun `searchNews filters news by content`() = runTest(testDispatcher) {
        val news1 = News(
            id = 1,
            content = "An article about space exploration and new telescopes.",
            checkDate = "2024-01-01",
            isFake = false
        )
        val news2 = News(
            id = 2,
            content = "Latest updates on financial markets.",
            checkDate = "2024-02-01",
            isFake = false
        )
        val news3 = News(
            id = 3,
            content = "New insights into quantum physics research.",
            checkDate = "2024-03-01",
            isFake = false
        )


        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())

            newsInDaoFlow.value = listOf(news1, news2, news3)
            advanceUntilIdle()

            assertEquals(NewsUiState(allNews = listOf(news1, news2, news3)), awaitItem())

            viewModel.searchNews("space")
            advanceUntilIdle()

            val filteredUiState = awaitItem()
            assertEquals(1, filteredUiState.allNews?.size)
            assertEquals(news1, filteredUiState.allNews?.first())
            assertEquals("space", viewModel.searchQuery.first())

            viewModel.searchNews("financial")
            advanceUntilIdle()
            val filteredUiState2 = awaitItem()
            assertEquals(1, filteredUiState2.allNews?.size)
            assertEquals(news2, filteredUiState2.allNews?.first())
            assertEquals("financial", viewModel.searchQuery.first())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchNews with empty query returns all news`() = runTest(testDispatcher) {
        val news1 =
            News(id = 1, content = "First news item.", checkDate = "2024-01-01", isFake = false)
        val news2 =
            News(id = 2, content = "Second news item.", checkDate = "2024-02-01", isFake = false)

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())
            newsInDaoFlow.value = listOf(news1, news2)
            advanceUntilIdle()
            assertEquals(NewsUiState(allNews = listOf(news1, news2)), awaitItem())

            viewModel.searchNews("some filter")
            advanceUntilIdle()
            assertEquals(0, awaitItem().allNews?.size)

            viewModel.searchNews("")
            advanceUntilIdle()

            val allNewsUiState = awaitItem()
            assertEquals(2, allNewsUiState.allNews?.size)
            assertEquals(listOf(news1, news2), allNewsUiState.allNews)
            assertEquals("", viewModel.searchQuery.first())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchNews is case insensitive`() = runTest(testDispatcher) {
        val newsItemMatching = News(
            id = 1,
            content = "A story about climate change impacts.",
            checkDate = "2024-04-01",
            isFake = false
        )
        val newsItemNotMatching = News(
            id = 2,
            content = "An article about space exploration.",
            checkDate = "2024-04-02",
            isFake = false
        )

        viewModel.newsUiState.test {
            assertEquals(NewsUiState(allNews = emptyList()), awaitItem())

            newsInDaoFlow.value = listOf(newsItemMatching, newsItemNotMatching)
            advanceUntilIdle()

            assertEquals(
                NewsUiState(allNews = listOf(newsItemMatching, newsItemNotMatching)),
                awaitItem()
            )

            viewModel.searchNews("ClImAtE")
            advanceUntilIdle()

            val uiState = awaitItem()
            assertEquals(1, uiState.allNews?.size)
            assertEquals(newsItemMatching, uiState.allNews?.first())
            assertEquals("ClImAtE", viewModel.searchQuery.first())

            cancelAndConsumeRemainingEvents()
        }
    }
}