package com.learn_japanese_with_music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.learn_japanese_with_music.core.theme.appTheme
import com.learn_japanese_with_music.features.lyrics.processor.JapaneseProcessor
import com.learn_japanese_with_music.features.lyrics.repository.LyricsRepository
import com.learn_japanese_with_music.features.lyrics.ui.LyricPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val japaneseProcessor = JapaneseProcessor.getInstance()
    private val repository = LyricsRepository(japaneseProcessor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用單例初始化
        lifecycleScope.launch(Dispatchers.IO) {
            japaneseProcessor.initialize(this@MainActivity)
        }
        
        enableEdgeToEdge()
        setContent {
            appTheme {
                LyricPage(repository)
            }
        }
    }
}
