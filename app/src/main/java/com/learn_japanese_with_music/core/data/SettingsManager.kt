package com.learn_japanese_with_music.core.data

import android.content.Context
import android.content.SharedPreferences
import com.learn_japanese_with_music.BuildConfig
import com.worksap.nlp.sudachi.Tokenizer

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GENIUS_TOKEN = "genius_api_token"
        private const val KEY_GEMINI_KEY = "gemini_api_key"
        private const val KEY_GEMINI_MODEL = "gemini_model_name"
        private const val KEY_SUDACHI_MODE = "sudachi_split_mode"
    }

    var geniusApiToken: String
        get() = prefs.getString(KEY_GENIUS_TOKEN, BuildConfig.GENIUS_API_TOKEN) ?: ""
        set(value) = prefs.edit().putString(KEY_GENIUS_TOKEN, value).apply()

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_KEY, BuildConfig.GEMINI_API_TOKEN) ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_KEY, value).apply()

    var geminiModel: String
        get() = prefs.getString(KEY_GEMINI_MODEL, "gemini-3.1-flash-lite") ?: "gemini-3.1-flash-lite"
        set(value) = prefs.edit().putString(KEY_GEMINI_MODEL, value).apply()

    var sudachiSplitMode: Tokenizer.SplitMode
        get() {
            val modeName = prefs.getString(KEY_SUDACHI_MODE, Tokenizer.SplitMode.C.name)
            return try {
                Tokenizer.SplitMode.valueOf(modeName ?: Tokenizer.SplitMode.C.name)
            } catch (e: Exception) {
                Tokenizer.SplitMode.C
            }
        }
        set(value) = prefs.edit().putString(KEY_SUDACHI_MODE, value.name).apply()
}
