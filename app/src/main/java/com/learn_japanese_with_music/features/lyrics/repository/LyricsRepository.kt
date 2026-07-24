package com.learn_japanese_with_music.features.lyrics.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import com.learn_japanese_with_music.core.data.SettingsManager
import com.learn_japanese_with_music.core.network.RetrofitClient
import com.learn_japanese_with_music.features.lyrics.model.GeniusSong
import com.learn_japanese_with_music.features.lyrics.model.SongData
import com.learn_japanese_with_music.features.lyrics.processor.JapaneseProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class LyricsRepository(private val japaneseProcessor: JapaneseProcessor, private var settingsManager: SettingsManager) {

    /**
     * 搜尋歌曲列表，包含過濾、排序與分頁
     */
    suspend fun searchSongs(query: String, page: Int = 1): List<GeniusSong> = withContext(Dispatchers.IO) {
        val searchResponse = RetrofitClient.geniusService.searchSongs(query, page)
        val songs = searchResponse.response.hits.map { it.result }
        
        // 1. 過濾掉以 Genius 開頭的歌手 (如 Genius Romanizations, Genius English Translations 等)
        val filteredSongs = songs.filter { !it.primary_artist.name.startsWith("Genius") }
        
        // 2. 優先顯示標題或歌手包含日文的歌曲
        filteredSongs.sortedByDescending { song ->
            val hasJapanese = containsJapanese(song.title) || containsJapanese(song.primary_artist.name)
            if (hasJapanese) 1 else 0
        }
    }

    private fun containsJapanese(text: String): Boolean {
        return text.any { char ->
            // 檢查是否為平假名、片假名或常用日文漢字區域
            char in '\u3040'..'\u309F' || // 平假名
            char in '\u30A0'..'\u30FF' || // 片假名
            char in '\u4E00'..'\u9FAF'    // 漢字
        }
    }

    /**
     * 根據 URL 抓取並處理歌詞
     */
    suspend fun fetchLyricsFromUrl(url: String): SongData = withContext(Dispatchers.IO) {
        // 1. Scrape lyrics and metadata
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .get()

        // 嘗試抓取歌名與歌手 (從 Genius 的特定標籤或標題)
        val title = doc.select("h1").first()?.text() ?: "Unknown Title"
        val artist = doc.select("a[href*=/artists/]").first()?.text() ?: "Unknown Artist"
        val cover = doc.select("img[alt^='Cover art for']").first()?.attr("src") ?: "Cover Not Found"

        //抓取歌詞並去掉冗於
        val lyricContainers = doc.select("div[data-lyrics-container=true]")
        val rawLyrics = if (lyricContainers.isNotEmpty()) {
            lyricContainers.select("div[data-exclude-from-selection=true]").remove()
            lyricContainers.joinToString("\n") { container ->
                container.select("br").append("\\n")
                container.text().replace("\\n", "\n")
            }
        } else{
            doc.selectFirst("div.lyrics")?.text() ?: ""
        }

        // 2. Process each line
        val filteredLines = rawLyrics.lines()
            .filter { it.isNotBlank() && !it.startsWith("[") } // 移除 [Verse], [Chorus] 等標籤
            
        val processedLyrics = filteredLines
            .map { japaneseProcessor.processLine(it, settingsManager.sudachiSplitMode) }

        SongData(
            title = title,
            artist = artist,
            lyrics = processedLyrics,
            cover = cover,
            rawLyrics = filteredLines
        )
    }
}
