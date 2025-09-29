package com.example.thesisapp.data.detection_model

import retrofit2.http.Body
import retrofit2.http.POST

interface ModelApiService {
    @POST("predict")
    suspend fun getDetection(@Body request: NewsRequest): PredictionResponse
}