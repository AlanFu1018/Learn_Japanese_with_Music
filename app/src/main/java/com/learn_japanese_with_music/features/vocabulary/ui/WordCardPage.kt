package com.learn_japanese_with_music.features.vocabulary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.learn_japanese_with_music.core.components.HomeRectangleButton
import com.learn_japanese_with_music.core.components.SearchBar
import com.learn_japanese_with_music.core.data.SettingsManager
import com.learn_japanese_with_music.core.data.database.AppDatabase
import com.learn_japanese_with_music.core.data.database.SavedWord
import com.learn_japanese_with_music.features.lyrics.model.LyricSegment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class WordCategoryMode { All, POS, Song }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordCardPage(
    settingsManager: SettingsManager,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var categoryMode by remember { mutableStateOf(WordCategoryMode.All) }
    var savedWords by remember { mutableStateOf<List<SavedWord>>(emptyList()) }
    
    // For nested navigation (e.g., viewing words of a specific song)
    var selectedCategoryValue by remember { mutableStateOf<String?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    
    // Word detail dialog state
    var selectedWordForDetail by remember { mutableStateOf<SavedWord?>(null) }
    val sheetState = rememberModalBottomSheetState()
    
    // 搜尋狀態
    var searchResults by remember { mutableStateOf<List<SavedWord>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible

    // 搜尋函式
    fun performSearch() {
        focusManager.clearFocus()
        if (searchQuery.isBlank()) {
            isSearching = false
            return
        }
        
        searchResults = savedWords.filter {
            it.word.contains(searchQuery, ignoreCase = true) || 
            it.generalMeaning.contains(searchQuery, ignoreCase = true) ||
            it.contextualMeaning.contains(searchQuery, ignoreCase = true)
        }
        isSearching = true
    }

    // Fetch words on launch or when database changes
    LaunchedEffect(Unit) {
        savedWords = withContext(Dispatchers.IO) {
            database.savedWordDao().getAllSavedWords()
        }
    }

    // 當鍵盤收合時，主動清除焦點以隱藏游標
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
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
            // Top Bar: Home + Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeRectangleButton(onClick = onMenuClick)
                Spacer(modifier = Modifier.width(12.dp))
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { 
                        searchQuery = it
                        if (it.isBlank()) isSearching = false
                    },
                    performSearch = { performSearch() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector
            Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                Button(
                    onClick = { isMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),

                ) {
                    Row(
                        modifier = Modifier.offset(-9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(isMenuExpanded)
                            Icon(imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "category selector down",
                            )
                        else
                            Icon(imageVector = Icons.Default.ArrowRight,
                                contentDescription = "category selector right",
                            )
                        Text(
                            text = "分類方式 : ${when(categoryMode) {
                                WordCategoryMode.All -> "全部"
                                WordCategoryMode.POS -> "詞性"
                                WordCategoryMode.Song -> "歌曲"
                            }}",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.45f)
                ) {
                    WordCategoryMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = { 
                                Text(when(mode) {
                                    WordCategoryMode.All -> "全部"
                                    WordCategoryMode.POS -> "詞性"
                                    WordCategoryMode.Song -> "歌曲"
                                })
                            },
                            onClick = {
                                categoryMode = mode
                                selectedCategoryValue = null
                                isMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (isSearching) {
                    // 顯示搜尋結果
                    Column {
                        Text(
                            text = "搜尋結果: \"$searchQuery\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SavedWordGrid(searchResults) { selectedWordForDetail = it }
                    }
                    
                    androidx.activity.compose.BackHandler {
                        isSearching = false
                        searchQuery = ""
                    }
                } else if (selectedCategoryValue != null) {
                    // Nested View: List of words in a specific category
                    val nestedWords = savedWords.filter {
                        when(categoryMode) {
                            WordCategoryMode.POS -> it.partOfSpeech.contains(selectedCategoryValue!!)
                            WordCategoryMode.Song -> it.songTitle == selectedCategoryValue
                            else -> true
                        }
                    }
                    
                    Column {
                        Text(
                            text = "分類: $selectedCategoryValue",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SavedWordGrid(nestedWords) { selectedWordForDetail = it }
                    }
                    
                    // Back button for nested view
                    androidx.activity.compose.BackHandler {
                        selectedCategoryValue = null
                    }
                } else {
                    // Main View
                    when (categoryMode) {
                        WordCategoryMode.All -> {
                            SavedWordGrid(savedWords) { selectedWordForDetail = it }
                        }
                        WordCategoryMode.POS -> {
                            val posGroups = savedWords.flatMap { 
                                try { Gson().fromJson<List<String>>(it.partOfSpeech, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type) } 
                                catch (e: Exception) { emptyList() }
                            }.distinct().filter { it != "*" }
                            
                            CategoryGrid(posGroups, null) { selectedCategoryValue = it }
                        }
                        WordCategoryMode.Song -> {
                            val songGroups = savedWords.groupBy { it.songTitle }
                            val songList = songGroups.map { it.key to it.value.first().songCover }
                            
                            CategoryGrid(songList.map { it.first }, songList) { selectedCategoryValue = it }
                        }
                    }
                }
            }
        }

        // Word Detail BottomSheet
        if (selectedWordForDetail != null) {
            ModalBottomSheet(
                onDismissRequest = { 
                    selectedWordForDetail = null
                    // Refresh words list in case something was deleted or notes changed
                    scope.launch {
                        savedWords = withContext(Dispatchers.IO) {
                            database.savedWordDao().getAllSavedWords()
                        }
                    }
                },
                sheetState = sheetState
            ) {
                val word = selectedWordForDetail!!
                val posList: List<String> = try { 
                    Gson().fromJson(word.partOfSpeech, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type) 
                } catch (e: Exception) { emptyList() }
                
                VocabularyCardContent(
                    segment = LyricSegment(word.word, word.reading, posList),
                    contextLine = word.contextLine,
                    settingsManager = settingsManager,
                    splitMode = word.splitMode,
                    songTitle = word.songTitle,
                    songArtist = word.songArtist,
                    songCover = word.songCover
                )
            }
        }
    }
}

@Composable
fun SavedWordGrid(words: List<SavedWord>, onWordClick: (SavedWord) -> Unit) {
    if (words.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("查無單字", color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(words) { word ->
                WordCardItem(word, onWordClick)
            }
        }
    }
}

@Composable
fun WordCardItem(word: SavedWord, onClick: (SavedWord) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clickable { onClick(word) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background (Song cover with overlay)
            AsyncImage(
                model = word.songCover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 50f
                        )
                    )
            )
            
            // Text info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = word.generalMeaning,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<String>, 
    songData: List<Pair<String, String>>? = null,
    onCategoryClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val coverUrl = songData?.find { it.first == category }?.second ?: ""
            CategoryCard(category, coverUrl, onCategoryClick)
        }
    }
}

@Composable
fun CategoryCard(title: String, coverUrl: String, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clickable { onClick(title) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (coverUrl.isNotEmpty()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
