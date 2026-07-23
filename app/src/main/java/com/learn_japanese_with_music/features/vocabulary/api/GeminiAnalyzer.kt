package com.learn_japanese_with_music.features.vocabulary.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.learn_japanese_with_music.features.vocabulary.model.WordAnalysis

class GeminiAnalyzer(private val apiKey: String, private val modelName: String) {
    private val gson = Gson()
    
    private val model = GenerativeModel(
        modelName = modelName,
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    suspend fun analyzeWord(word: String, contextLine: String, partOfSpeech: List<String>): WordAnalysis? {
        if (apiKey.isBlank()) return null

        val prompt = """
            你是一位專業的日文老師與翻譯。請針對以下日文單字進行分析。
            單字：$word
            歌詞上下文：$contextLine
            分詞器提供的詞性參考：${partOfSpeech.joinToString("/")}
            
            請以 JSON 格式回傳以下資訊（繁體中文）：
            1. jlpt_level: JLPT 級別 (N1-N5 或 "unknown")
            2. general_meaning: 通用的單字意思（請盡量貼合歌詞語境的解釋，減少閱讀割裂感）
            3. contextual_meaning: 在這段歌詞中的具體意思與用法（若與通用意思相同則簡述，若有特殊用法請詳述）
            4. common_usages: 2-3 個與此單字「在歌詞中用法」相關的其他常見用法或例句。
               每個例句必須是一個包含以下欄位的 JSON 物件：
               - japanese: 日文句子
               - reading: 漢字的平假名讀音
               - translation: 中文翻譯
            
            請僅回傳 JSON 內容，不要包含 Markdown 語法標籤。
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            val jsonText = response.text ?: return null
            Log.d("GeminiAnalyzer", "Response: ${jsonText}")
            gson.fromJson(jsonText, WordAnalysis::class.java)
        } catch (e: Exception) {
            Log.e("GeminiAnalyzer", "Error analyzing word", e)
            null
        }
    }
}
