package com.learn_japanese_with_music.features.lyrics.repository

import com.learn_japanese_with_music.core.network.RetrofitClient
import com.learn_japanese_with_music.features.lyrics.model.GeniusSong
import com.learn_japanese_with_music.features.lyrics.model.SongData
import com.learn_japanese_with_music.features.lyrics.processor.JapaneseProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class LyricsRepository(private val japaneseProcessor: JapaneseProcessor) {

    /**
     * 搜尋歌曲列表
     */
    suspend fun searchSongs(query: String): List<GeniusSong> = withContext(Dispatchers.IO) {
        val searchResponse = RetrofitClient.geniusService.searchSongs(query)
        searchResponse.response.hits.map { it.result }
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
        val cover = doc.select("img[alt^='Cover art for']").first()?.attr("src") ?: "https://www.vecteezy.com/vector-art/29796931-music-icon-in-trendy-flat-style-isolated-on-white-background-music-silhouette-symbol-for-your-website-design-logo-app-ui-vector-illustration-eps10"

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
        val processedLyrics = rawLyrics.lines()
            .filter { it.isNotBlank() && !it.startsWith("[") } // 移除 [Verse], [Chorus] 等標籤
            .map { japaneseProcessor.processLine(it) }

        SongData(
            title = title,
            artist = artist,
            lyrics = processedLyrics,
            cover = cover
        )
    }
}
