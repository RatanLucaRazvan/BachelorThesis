package com.example.thesisapp.data.news

import android.database.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow

class LocalNewsRepository(private val newsDao: NewsDao) : NewsRepository {
    override suspend fun insert(news: News) {
        newsDao.insert(news)
    }

    override fun getNewsStream(): Flow<List<News>> {
        return newsDao.getNews()
    }

    override suspend fun deleteNews(newsId: Int) {
        newsDao.delete(newsId)
    }

    override fun searchNewsStream(query: String): Flow<List<News>> {
        return newsDao.searchNews(query)
    }
}