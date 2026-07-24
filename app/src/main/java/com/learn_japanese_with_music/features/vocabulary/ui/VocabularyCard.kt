package com.learn_japanese_with_music.features.vocabulary.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learn_japanese_with_music.core.data.SettingsManager
import com.learn_japanese_with_music.core.data.database.AppDatabase
import com.learn_japanese_with_music.core.data.database.SavedWord
import com.learn_japanese_with_music.core.data.database.WordCache
import com.learn_japanese_with_music.features.lyrics.model.LyricSegment
import com.learn_japanese_with_music.features.vocabulary.api.GeminiAnalyzer
import com.learn_japanese_with_music.features.vocabulary.model.UsageExample
import com.learn_japanese_with_music.features.vocabulary.model.WordAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VocabularyCardContent(
    segment: LyricSegment,
    contextLine: String,
    settingsManager: SettingsManager,
    splitMode: String,
    songTitle: String = "Unknown",
    songArtist: String = "Unknown",
    songCover: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val gson = remember { Gson() }
    val scope = rememberCoroutineScope()
    
    var analysis by remember { mutableStateOf<WordAnalysis?>(null) }
    var isSaved by remember { mutableStateOf(false) }
    var userNotes by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(segment, contextLine, splitMode) {
        isLoading = true
        error = null
        
        // 1. Try to get from SavedWords (Highest priority)
        val saved = withContext(Dispatchers.IO) {
            database.savedWordDao().getSavedWord(segment.text, contextLine, splitMode)
        }

        if (saved != null) {
            isSaved = true
            userNotes = saved.notes
            val listType = object : TypeToken<List<UsageExample>>() {}.type
            analysis = WordAnalysis(
                jlptLevel = saved.jlptLevel,
                generalMeaning = saved.generalMeaning,
                contextualMeaning = saved.contextualMeaning,
                commonUsages = try { gson.fromJson(saved.commonUsages, listType) } catch (e: Exception) { emptyList() }
            )
            isLoading = false
            return@LaunchedEffect
        }

        // 2. Try to get from cache
        val cache = withContext(Dispatchers.IO) {
            database.wordCacheDao().getWordCache(segment.text, contextLine, splitMode)
        }

        if (cache != null) {
            val listType = object : TypeToken<List<UsageExample>>() {}.type
            analysis = WordAnalysis(
                jlptLevel = cache.jlptLevel,
                generalMeaning = cache.generalMeaning,
                contextualMeaning = cache.contextualMeaning,
                commonUsages = try { gson.fromJson(cache.commonUsages, listType) } catch (e: Exception) { emptyList() }
            )
            isLoading = false
        } else {
            // 3. Fetch from Gemini
            val apiKey = settingsManager.geminiApiKey
            val modelName = settingsManager.geminiModel
            
            if (apiKey.isBlank()) {
                error = "Please set Gemini API Key in Settings"
                isLoading = false
                return@LaunchedEffect
            }

            val analyzer = GeminiAnalyzer(apiKey, modelName)
            val result = withContext(Dispatchers.IO) {
                analyzer.analyzeWord(segment.text, contextLine, segment.partOfSpeech)
            }

            if (result != null) {
                analysis = result
                // Save to cache
                withContext(Dispatchers.IO) {
                    database.wordCacheDao().insertWordCache(
                        WordCache(
                            word = segment.text,
                            contextLine = contextLine,
                            splitMode = splitMode,
                            jlptLevel = result.jlptLevel,
                            generalMeaning = result.generalMeaning,
                            contextualMeaning = result.contextualMeaning,
                            commonUsages = gson.toJson(result.commonUsages)
                        )
                    )
                }
            } else {
                error = "Failed to get analysis from Gemini"
            }
            isLoading = false
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Word and Reading
            if (segment.reading.isNotEmpty()) {
                Text(
                    text = segment.reading,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = segment.text,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // POS Badges
            if (segment.partOfSpeech.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    segment.partOfSpeech.filter { it != "*" }.take(2).forEach { pos ->
                        Badge(text = pos, color = MaterialTheme.colorScheme.primaryContainer)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Gemini is analyzing...", style = MaterialTheme.typography.bodyMedium)
            } else if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            } else if (analysis != null) {
                AnalysisDetails(analysis!!)
                
                if (isSaved) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionTitle("個人筆記")
                    OutlinedTextField(
                        value = userNotes,
                        onValueChange = { 
                            userNotes = it 
                            scope.launch(Dispatchers.IO) {
                                database.savedWordDao().updateNotes(segment.text, contextLine, splitMode, it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("在此輸入筆記...") },
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Action Button (Top Right)
        if (!isLoading && analysis != null) {
            IconButton(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        if (isSaved) {
                            database.savedWordDao().deleteSavedWordById(segment.text, contextLine, splitMode)
                            isSaved = false
                        } else {
                            val newSaved = SavedWord(
                                word = segment.text,
                                contextLine = contextLine,
                                splitMode = splitMode,
                                reading = segment.reading,
                                partOfSpeech = gson.toJson(segment.partOfSpeech),
                                jlptLevel = analysis!!.jlptLevel,
                                generalMeaning = analysis!!.generalMeaning,
                                contextualMeaning = analysis!!.contextualMeaning,
                                commonUsages = gson.toJson(analysis!!.commonUsages),
                                notes = userNotes,
                                songTitle = songTitle,
                                songArtist = songArtist,
                                songCover = songCover
                            )
                            database.savedWordDao().insertSavedWord(newSaved)
                            isSaved = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Delete else Icons.Default.Add,
                    contentDescription = if (isSaved) "Remove" else "Save",
                    tint = if (isSaved) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AnalysisDetails(analysis: WordAnalysis) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            if (analysis.jlptLevel.lowercase() != "unknown") {
                Badge(text = "JLPT ${analysis.jlptLevel}", color = MaterialTheme.colorScheme.tertiaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("一般意思")
        Text(text = analysis.generalMeaning, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("歌詞中的用法")
        Text(text = analysis.contextualMeaning, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 35.sp))

        if (analysis.commonUsages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("常見用法")
            analysis.commonUsages.forEach { usage ->
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(
                        text = usage.japanese,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = usage.reading,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = usage.translation,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 7.dp)
    )
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
