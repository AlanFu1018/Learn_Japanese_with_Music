package com.learn_japanese_with_music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.learn_japanese_with_music.lyrics_display.JapaneseProcessor
import com.learn_japanese_with_music.lyrics_display.LyricsRepository
import com.learn_japanese_with_music.ui.pages.LyricPage
import com.learn_japanese_with_music.ui.theme.appTheme

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val japaneseProcessor = JapaneseProcessor.getInstance()
    private val repository = LyricsRepository(japaneseProcessor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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