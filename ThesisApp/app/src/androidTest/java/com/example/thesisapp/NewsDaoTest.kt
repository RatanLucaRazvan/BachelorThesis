package com.example.thesisapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.thesisapp.data.news.News
import com.example.thesisapp.data.news.NewsDao
import com.example.thesisapp.data.news.NewsDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class NewsDaoTest {

    private lateinit var newsDao: NewsDao
    private lateinit var db: NewsDatabase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, NewsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        newsDao = db.newsDao()
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun testDaoIsNotNull() {
        assertNotNull(newsDao)
    }

    @Test
    fun insertAndGetNewsRetrievesSingleNewsItem() = runTest(testDispatcher) {
        val news =
            News(id = 0, content = "Test content 1", checkDate = "2023-01-01", isFake = false)
        newsDao.insert(news)
        advanceUntilIdle()

        newsDao.getNews().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(news.content, items.first().content)
            assertEquals(news.checkDate, items.first().checkDate)
            assertEquals(news.isFake, items.first().isFake)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun insertAndGetNewsRetrievesMultipleNewsItems() = runTest(testDispatcher) {
        val news1 = News(id = 0, content = "Content One", checkDate = "2023-01-01", isFake = false)
        val news2 = News(id = 0, content = "Content Two", checkDate = "2023-01-02", isFake = true)

        newsDao.insert(news1)
        newsDao.insert(news2)
        advanceUntilIdle()

        newsDao.getNews().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertTrue(items.any { it.content == "Content One" })
            assertTrue(items.any { it.content == "Content Two" })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun deleteRemovesNewsItemById() = runTest(testDispatcher) {
        val news1 =
            News(id = 0, content = "To be deleted", checkDate = "2023-01-01", isFake = false)
        val news2 = News(id = 0, content = "To remain", checkDate = "2023-01-02", isFake = true)

        newsDao.insert(news1)
        newsDao.insert(news2)
        advanceUntilIdle()

        newsDao.getNews().test {
            assertEquals(2, awaitItem().size)
            cancelAndConsumeRemainingEvents()
        }

        newsDao.delete(1)
        advanceUntilIdle()

        newsDao.getNews().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(news2.content, items.first().content)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun searchNewsFindsMatchingContentCaseInsensitive() = runTest(testDispatcher) {
        val news1 = News(
            id = 0,
            content = "A story about climate change impacts.",
            checkDate = "2024-04-01",
            isFake = false
        )
        val news2 = News(
            id = 0,
            content = "An article about space exploration.",
            checkDate = "2024-04-02",
            isFake = false
        )
        val news3 = News(
            id = 0,
            content = "Another story about climate policy.",
            checkDate = "2024-04-03",
            isFake = false
        )

        newsDao.insert(news1)
        newsDao.insert(news2)
        newsDao.insert(news3)
        advanceUntilIdle()

        newsDao.searchNews("ClImAtE").test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertTrue(items.any { it.content.contains("climate change") })
            assertTrue(items.any { it.content.contains("climate policy") })
            cancelAndConsumeRemainingEvents()
        }

        newsDao.searchNews("SPACE").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(news2.content, items.first().content)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun searchNewsReturnsEmptyListForNoMatch() = runTest(testDispatcher) {
        val news =
            News(id = 0, content = "Some unique content", checkDate = "2023-01-01", isFake = false)
        newsDao.insert(news)
        advanceUntilIdle()

        newsDao.searchNews("nonexistentword").test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getNewsReturnsEmptyListWhenDatabaseIsEmpty() = runTest(testDispatcher) {
        newsDao.getNews().test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }
}