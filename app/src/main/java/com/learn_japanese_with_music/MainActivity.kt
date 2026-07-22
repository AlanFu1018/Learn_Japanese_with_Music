package com.learn_japanese_with_music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.learn_japanese_with_music.core.data.SettingsManager
import com.learn_japanese_with_music.core.network.RetrofitClient
import com.learn_japanese_with_music.core.theme.appTheme
import com.learn_japanese_with_music.features.lyrics.processor.JapaneseProcessor
import com.learn_japanese_with_music.features.lyrics.repository.LyricsRepository
import com.learn_japanese_with_music.features.lyrics.ui.LyricPage
import com.learn_japanese_with_music.features.settings.ui.SettingsPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Screen { Search, Settings }

class MainActivity : ComponentActivity() {
    private val japaneseProcessor = JapaneseProcessor.getInstance()
    private val repository = LyricsRepository(japaneseProcessor)
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsManager = SettingsManager(this)
        
        // 讓 RetrofitClient 讀取 SettingsManager 中的 Token
        RetrofitClient.tokenProvider = { settingsManager.geniusApiToken }
        
        // 使用單例初始化
        lifecycleScope.launch(Dispatchers.IO) {
            japaneseProcessor.initialize(this@MainActivity)
        }
        
        enableEdgeToEdge()
        setContent {
            appTheme {
                var currentScreen by remember { mutableStateOf(Screen.Search) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Column(
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Learn Japanese with Song",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))

                                Spacer(modifier = Modifier.height(16.dp))
                                
                                NavigationDrawerItem(
                                    label = { Text("Search for lyrics", style = MaterialTheme.typography.titleMedium) },
                                    selected = currentScreen == Screen.Search,
                                    onClick = {
                                        currentScreen = Screen.Search
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(Icons.Default.Search, contentDescription = null) }
                                )
                                
                                NavigationDrawerItem(
                                    label = { Text("Settings", style = MaterialTheme.typography.titleMedium) },
                                    selected = currentScreen == Screen.Settings,
                                    onClick = {
                                        currentScreen = Screen.Settings
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                                )
                            }
                        }
                    }
                ) {
                    when (currentScreen) {
                        Screen.Search -> {
                            LyricPage(
                                repository = repository,
                                settingsManager = settingsManager,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }
                        Screen.Settings -> {
                            SettingsPage(
                                settingsManager = settingsManager,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }
                    }
                }
            }
        }
    }
}
