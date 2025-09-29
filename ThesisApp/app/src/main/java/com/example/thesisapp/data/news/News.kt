package com.example.thesisapp.data.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "news")
data class News(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val checkDate: String,
    val isFake: Boolean,
)

