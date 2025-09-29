package com.example.thesisapp.utils

fun countWords(text: String): Int {
    val trimmedText = text.trim()

    if (trimmedText.isEmpty()) {
        return 0
    }

    return trimmedText.split("\\s+".toRegex()).count()
}