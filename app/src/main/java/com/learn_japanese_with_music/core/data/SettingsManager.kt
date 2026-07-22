package com.learn_japanese_with_music.core.data

import android.content.Context
import android.content.SharedPreferences
import com.learn_japanese_with_music.BuildConfig
import com.worksap.nlp.sudachi.Tokenizer

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GENIUS_TOKEN = "genius_api_token"
        private const val KEY_SUDACHI_MODE = "sudachi_split_mode"
    }

    var geniusApiToken: String
        get() = prefs.getString(KEY_GENIUS_TOKEN, BuildConfig.GENIUS_API_TOKEN) ?: "No Token has been provided recently"
        set(value) = prefs.edit().putString(KEY_GENIUS_TOKEN, value).apply()

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
