package com.learn_japanese_with_music.features.vocabulary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.learn_japanese_with_music.core.components.HomeRectangleButton
import com.learn_japanese_with_music.core.data.SettingsManager
import com.learn_japanese_with_music.core.data.database.AppDatabase
import com.learn_japanese_with_music.core.data.database.SavedWord
import com.learn_japanese_with_music.features.lyrics.model.LyricSegment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class WordCategoryMode { All, POS, Song }

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Word detail dialog state
    var selectedWordForDetail by remember { mutableStateOf<SavedWord?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Fetch words on launch or when database changes
    LaunchedEffect(Unit) {
        savedWords = withContext(Dispatchers.IO) {
            database.savedWordDao().getAllSavedWords()
        }
    }

    val filteredWords = savedWords.filter {
        it.word.contains(searchQuery, ignoreCase = true) || 
        it.generalMeaning.contains(searchQuery, ignoreCase = true) ||
        it.contextualMeaning.contains(searchQuery, ignoreCase = true)
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    placeholder = { Text("搜尋單字或釋義...", style = MaterialTheme.typography.bodyMedium) },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                WordCategoryMode.values().forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = categoryMode == mode,
                        onClick = { 
                            categoryMode = mode 
                            selectedCategoryValue = null // Reset nested view
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = WordCategoryMode.values().size)
                    ) {
                        Text(when(mode) {
                            WordCategoryMode.All -> "全部"
                            WordCategoryMode.POS -> "詞性"
                            WordCategoryMode.Song -> "歌曲"
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (selectedCategoryValue != null) {
                    // Nested View: List of words in a specific category
                    val nestedWords = filteredWords.filter {
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
                            SavedWordGrid(filteredWords) { selectedWordForDetail = it }
                        }
                        WordCategoryMode.POS -> {
                            val posGroups = filteredWords.flatMap { 
                                try { Gson().fromJson<List<String>>(it.partOfSpeech, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type) } 
                                catch (e: Exception) { emptyList() }
                            }.distinct().filter { it != "*" }
                            
                            CategoryGrid(posGroups, null) { selectedCategoryValue = it }
                        }
                        WordCategoryMode.Song -> {
                            val songGroups = filteredWords.groupBy { it.songTitle }
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
