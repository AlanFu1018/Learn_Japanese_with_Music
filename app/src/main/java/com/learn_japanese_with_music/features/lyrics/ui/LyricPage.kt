package com.learn_japanese_with_music.features.lyrics.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.ai.client.generativeai.type.content
import com.learn_japanese_with_music.core.components.HomeRectangleButton
import com.learn_japanese_with_music.core.components.SearchBar
import com.learn_japanese_with_music.core.data.SettingsManager
import com.learn_japanese_with_music.features.lyrics.model.GeniusSong
import com.learn_japanese_with_music.features.lyrics.model.LyricSegment
import com.learn_japanese_with_music.features.lyrics.model.SongData
import com.learn_japanese_with_music.features.lyrics.processor.JapaneseProcessor
import com.learn_japanese_with_music.features.lyrics.repository.LyricsRepository
import com.learn_japanese_with_music.features.vocabulary.ui.VocabularyCardContent
import com.worksap.nlp.sudachi.Tokenizer
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LyricPage(
    repository: LyricsRepository,
    settingsManager: SettingsManager,
    currentMode: Tokenizer.SplitMode,
    onMenuClick: () -> Unit
) {
    var query by remember { mutableStateOf("前前前世") }
    var searchResults by remember { mutableStateOf<List<GeniusSong>>(emptyList()) }
    var lyrics by remember { mutableStateOf<SongData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isMoreLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var currentPage by remember { mutableStateOf(1) }
    var isFullListLoaded by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible

    // 單字卡相關狀態
    var selectedSegment by remember { mutableStateOf<LyricSegment?>(null) }
    var selectedContextLine by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val japaneseProcessor = JapaneseProcessor.getInstance()

    // 當 Settings 中的模式改變時，即時更新目前的歌詞顯示
    LaunchedEffect(currentMode) {
        lyrics?.let { currentLyrics ->
            if (currentLyrics.rawLyrics.isNotEmpty()) {
                val newProcessedLyrics = currentLyrics.rawLyrics.map { line ->
                    japaneseProcessor.processLine(line, currentMode)
                }
                lyrics = currentLyrics.copy(lyrics = newProcessedLyrics)
            }
        }
    }

    // 當鍵盤收合時，主動清除焦點以隱藏游標
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    // 封裝搜尋邏輯
    fun performSearch(isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            focusManager.clearFocus()
            isLoading = true
            searchResults = emptyList()
            currentPage = 1
            isFullListLoaded = false
        } else {
            isMoreLoading = true
        }
        
        errorMessage = null
        lyrics = null
        
        scope.launch {
            try {
                val newResults = repository.searchSongs(query, if (isLoadMore) currentPage + 1 else 1)
                if (newResults.isEmpty()) {
                    isFullListLoaded = true
                } else {
                    searchResults = if (isLoadMore) searchResults + newResults else newResults
                    if (isLoadMore) currentPage++
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
                isMoreLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {

            // Search Row
            if (lyrics == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.padding(end = 8.dp)){
                        HomeRectangleButton(onClick = {
                            focusManager.clearFocus()
                            onMenuClick()
                        })
                    }

                    SearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        performSearch = { performSearch() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage?.let {
                if(settingsManager.geniusApiToken.isBlank())
                    Text(text = "Please enter your Genius API Access Token in the Settings",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium
                    )
                else
                    Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }

            if (lyrics != null) {
                BackHandler {
                    lyrics = null
                }

                //display lyrics
                LyricsDisplay(
                    songData = lyrics!!,
                    modifier = Modifier.weight(1f),
                    onSegmentClick = { segment, contextLine ->
                        selectedSegment = segment
                        selectedContextLine = contextLine
                        showBottomSheet = true
                    }
                )

                //show the result of searching
            } else if (searchResults.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(searchResults) { index, song ->
                        // 觸發加載更多 (滑動到接近末尾時)
                        if (index >= searchResults.size - 4 && !isMoreLoading && !isFullListLoaded) {
                            performSearch(isLoadMore = true)
                        }
                        
                        SearchResultItem(song = song) {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    lyrics = repository.fetchLyricsFromUrl(song.url)
                                } catch (e: Exception) {
                                    errorMessage = e.message
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                    
                    // 底部加載指示器
                    if (isMoreLoading) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            } else if (!isLoading && errorMessage == null) {
                Text(text = "Search for a song to see lyrics", style = MaterialTheme.typography.labelMedium)
            }
        }

        // word card
        if (showBottomSheet && selectedSegment != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                VocabularyCardContent(
                    segment = selectedSegment!!,
                    contextLine = selectedContextLine,
                    settingsManager = settingsManager,
                    splitMode = currentMode.name,
                    songTitle = lyrics?.title ?: "Unknown",
                    songArtist = lyrics?.artist ?: "Unknown",
                    songCover = lyrics?.cover ?: ""
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(song: GeniusSong, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if(song.song_art_image_thumbnail_url.equals("Cover Not Found"))
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Cover Not Found"
                )
            else
                AsyncImage(
                    model = song.song_art_image_thumbnail_url,
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            
            // 底部文字區域背景遮罩 (漸層)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f // 從一半左右開始漸層
                        )
                    )
            )

            // 文字內容
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.primary_artist.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
