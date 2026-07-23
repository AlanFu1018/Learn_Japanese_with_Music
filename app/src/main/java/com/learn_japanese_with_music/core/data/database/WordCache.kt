package com.learn_japanese_with_music.core.data.database

import androidx.room.Entity

@Entity(primaryKeys = ["word", "contextLine", "splitMode"])
data class WordCache(
    val word: String,
    val contextLine: String,
    val splitMode: String,
    val jlptLevel: String,
    val generalMeaning: String,
    val contextualMeaning: String,
    val commonUsages: String, // Store as JSON string (List of UsageExample)
    val timestamp: Long = System.currentTimeMillis()
)
