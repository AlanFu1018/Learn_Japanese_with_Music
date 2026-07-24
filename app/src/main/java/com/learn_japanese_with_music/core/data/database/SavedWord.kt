package com.learn_japanese_with_music.core.data.database

import androidx.room.Entity

@Entity(primaryKeys = ["word", "contextLine", "splitMode"])
data class SavedWord(
    val word: String,
    val contextLine: String,
    val splitMode: String,
    val reading: String,
    val partOfSpeech: String, // Store as JSON string or concatenated
    val jlptLevel: String,
    val generalMeaning: String,
    val contextualMeaning: String,
    val commonUsages: String, // Store as JSON string (List of UsageExample)
    val notes: String = "",
    val songTitle: String,
    val songArtist: String,
    val songCover: String,
    val timestamp: Long = System.currentTimeMillis()
)
