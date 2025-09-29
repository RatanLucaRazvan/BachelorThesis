package com.example.thesisapp.data.detection_model

class ModelRepository(
    private val modelApiService: ModelApiService,
) {
    suspend fun getDetection(request: NewsRequest): PredictionResponse {
        return modelApiService.getDetection(request)
    }
}