package com.learn_japanese_with_music.features.vocabulary.model

import com.google.gson.annotations.SerializedName

data class WordAnalysis(
    @SerializedName("jlpt_level") val jlptLevel: String,
    @SerializedName("general_meaning") val generalMeaning: String,
    @SerializedName("contextual_meaning") val contextualMeaning: String,
    @SerializedName("common_usages") val commonUsages: List<UsageExample>
)

data class UsageExample(
    @SerializedName("japanese") val japanese: String,
    @SerializedName("reading") val reading: String,
    @SerializedName("translation") val translation: String
)
