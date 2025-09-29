package com.example.thesisapp.data.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert
    suspend fun insert(news: News)

    @Query("SELECT * FROM news")
    fun getNews(): Flow<List<News>>

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM news WHERE content LIKE '%' || :query || '%'")
    fun searchNews(query: String): Flow<List<News>>
}