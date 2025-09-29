package com.example.thesisapp.data.news

interface NewsRepository {
    suspend fun insert(news: News)

    fun getNewsStream(): kotlinx.coroutines.flow.Flow<List<News>>

    suspend fun deleteNews(newsId: Int)

    fun searchNewsStream(query: String): kotlinx.coroutines.flow.Flow<List<News>>
}