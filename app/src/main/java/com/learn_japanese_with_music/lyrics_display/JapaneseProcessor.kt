package com.learn_japanese_with_music.lyrics_display

import android.content.Context
import android.util.Log
import com.worksap.nlp.sudachi.Dictionary
import com.worksap.nlp.sudachi.DictionaryFactory
import com.worksap.nlp.sudachi.Tokenizer
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

class JapaneseProcessor private constructor() {

    private var dictionary: Dictionary? = null
    private var tokenizer: Tokenizer? = null
    private val isInitializing = AtomicBoolean(false)
    private val isInitialized = AtomicBoolean(false)

    companion object {
        private const val TAG = "JapaneseProcessor"
        
        @Volatile
        private var instance: JapaneseProcessor? = null

        fun getInstance(): JapaneseProcessor {
            return instance ?: synchronized(this) {
                instance ?: JapaneseProcessor().also { instance = it }
            }
        }
    }

    /**
     * 初始化 Sudachi 字典。建議在背景執行緒呼叫。
     * 使用 applicationContext 避免 Activity 洩漏。
     */
    fun initialize(context: Context) {
        if (isInitialized.get() || isInitializing.getAndSet(true)) {
            return
        }

        try {
            val appContext = context.applicationContext
            val dictFile = File(appContext.filesDir, "system_full.dic")
            
            if (!dictFile.exists()) {
                Log.d(TAG, "Copying dictionary from assets to internal storage...")
                appContext.assets.open("system_full.dic").use { input ->
                    FileOutputStream(dictFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            Log.d(TAG, "Loading Sudachi dictionary: ${dictFile.absolutePath}")
            val settings = """
                {
                    "systemDict": "${dictFile.absolutePath}",
                    "inputTextPlugin": [
                        { "class": "com.worksap.nlp.sudachi.DefaultInputTextPlugin" }
                    ],
                    "oovProviderPlugin": [
                        { "class": "com.worksap.nlp.sudachi.MeCabOovProviderPlugin" }
                    ]
                }
            """.trimIndent()

            val dict = DictionaryFactory().create(settings)
            this.dictionary = dict
            this.tokenizer = dict.create()
            isInitialized.set(true)
            Log.i(TAG, "Sudachi dictionary loaded successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Sudachi dictionary", e)
        } finally {
            isInitializing.set(false)
        }
    }

    fun processLine(text: String): LyricLine {
        val currentTokenizer = tokenizer
        if (currentTokenizer == null) {
            Log.w(TAG, "Processor not initialized. Returning raw text.")
            return LyricLine(listOf(LyricSegment(text)))
        }
        
        val morphemes = currentTokenizer.tokenize(text)
        val segments = morphemes.map { morpheme ->
            val surface = morpheme.surface()
            val reading = morpheme.readingForm()
            val finalReading = if (needsReading(surface, reading)) reading else ""
            LyricSegment(text = surface, reading = finalReading)
        }
        return LyricLine(segments = segments)
    }

    private fun needsReading(surface: String, reading: String?): Boolean {
        if (reading == null || reading == "*" || surface == reading || reading.isEmpty()) return false
        return surface.any { it.isKanji() }
    }

    private fun Char.isKanji(): Boolean {
        return this in '\u4e00'..'\u9faf'
    }

    /**
     * 應用程式關閉時釋放資源
     */
    fun close() {
        try {
            dictionary?.close()
            dictionary = null
            tokenizer = null
            isInitialized.set(false)
            Log.d(TAG, "Sudachi dictionary closed.")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing dictionary", e)
        }
    }
}
