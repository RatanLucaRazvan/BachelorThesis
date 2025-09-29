package com.example.thesisapp.data

import android.content.Context
import com.example.thesisapp.data.detection_model.ModelApiService
import com.example.thesisapp.data.detection_model.ModelRepository
import com.example.thesisapp.data.news.LocalNewsRepository
import com.example.thesisapp.data.news.NewsDatabase
import com.example.thesisapp.data.news.NewsRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


interface AppContainer {
    val localNewsRepository: NewsRepository
    val modelRepository: ModelRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val baseUrl = "http://172.30.247.137:8000"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: ModelApiService by lazy {
        retrofit.create(ModelApiService::class.java)
    }

    override val modelRepository: ModelRepository by lazy {
        ModelRepository(retrofitService)
    }
    override val localNewsRepository: LocalNewsRepository by lazy {
        LocalNewsRepository(NewsDatabase.getDatabase(context).newsDao())
    }
}