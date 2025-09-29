package com.example.thesisapp.data.news

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [News::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var Instance: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, NewsDatabase::class.java, "news_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}