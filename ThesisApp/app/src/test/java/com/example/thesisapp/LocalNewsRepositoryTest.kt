package com.example.thesisapp

import app.cash.turbine.test
import com.example.thesisapp.data.news.News
import com.example.thesisapp.data.news.NewsDao
import com.example.thesisapp.data.news.LocalNewsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalNewsRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockNewsDao: NewsDao

    private lateinit var localNewsRepository: LocalNewsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockNewsDao = mockk()

        localNewsRepository = LocalNewsRepository(mockNewsDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insert calls newsDao insert with correct news item`() = runTest(testDispatcher) {
        val news = News(id = 0, content = "New article", checkDate = "2024-06-08", isFake = false)

        coEvery { mockNewsDao.insert(any()) } just Runs

        localNewsRepository.insert(news)
        advanceUntilIdle()

        coVerify(exactly = 1) { mockNewsDao.insert(news) }
    }

    @Test
    fun `getNewsStream returns flow from newsDao getNews`() = runTest(testDispatcher) {
        val newsList = listOf(
            News(id = 1, content = "Content A", checkDate = "2024-06-08", isFake = false),
            News(id = 2, content = "Content B", checkDate = "2024-06-09", isFake = true)
        )

        every { mockNewsDao.getNews() } returns flowOf(newsList)

        localNewsRepository.getNewsStream().test {
            assertEquals(newsList, awaitItem())
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 1) { mockNewsDao.getNews() }
    }

    @Test
    fun `deleteNews calls newsDao delete with correct id`() = runTest(testDispatcher) {
        val newsIdToDelete = 123

        coEvery { mockNewsDao.delete(any()) } just Runs

        localNewsRepository.deleteNews(newsIdToDelete)
        advanceUntilIdle()

        coVerify(exactly = 1) { mockNewsDao.delete(newsIdToDelete) }
    }

    @Test
    fun `searchNewsStream returns flow from newsDao searchNews`() = runTest(testDispatcher) {
        val query = "test"
        val filteredList = listOf(
            News(
                id = 1,
                content = "This is a test article",
                checkDate = "2024-06-08",
                isFake = false
            )
        )

        every { mockNewsDao.searchNews(query) } returns flowOf(filteredList)

        localNewsRepository.searchNewsStream(query).test {
            assertEquals(filteredList, awaitItem())
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 1) { mockNewsDao.searchNews(query) }
    }
}