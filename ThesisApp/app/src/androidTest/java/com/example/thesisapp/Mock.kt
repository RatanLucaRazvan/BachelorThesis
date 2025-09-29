package com.example.thesisapp

import com.example.thesisapp.data.detection_model.ModelApiService
import com.example.thesisapp.data.detection_model.PredictionResponse
import io.mockk.coEvery
import io.mockk.mockk


fun mockModelApiService(prediction: String): ModelApiService {
    val mockService = mockk<ModelApiService>()
    coEvery { mockService.getDetection(any()) } returns PredictionResponse(prediction)
    return mockService
}