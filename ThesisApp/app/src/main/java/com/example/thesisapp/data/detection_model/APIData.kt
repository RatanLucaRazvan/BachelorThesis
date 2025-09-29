package com.example.thesisapp.data.detection_model

import kotlinx.serialization.Serializable

@Serializable
data class NewsRequest(
    val content: String
)

@Serializable
data class PredictionResponse(
    val prediction: String
)