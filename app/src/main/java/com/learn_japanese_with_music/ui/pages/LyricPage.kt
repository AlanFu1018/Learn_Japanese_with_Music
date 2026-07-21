package com.learn_japanese_with_music.ui.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learn_japanese_with_music.ui.components.HomeRectangleButton
import com.learn_japanese_with_music.lyrics_display.SongData
import com.learn_japanese_with_music.lyrics_display.GeniusSong
import com.learn_japanese_with_music.lyrics_display.LyricsDisplay
import com.learn_japanese_with_music.lyrics_display.LyricsRepository
import kotlinx.coroutines.launch

@Composable
fun LyricPage(repository: LyricsRepository) {
    var query by remember { mutableStateOf("前前前世") }
    var searchResults by remember { mutableStateOf<List<GeniusSong>>(emptyList()) }
    var lyrics by remember { mutableStateOf<SongData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            lyrics = null
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Setting", style = MaterialTheme.typography.titleMedium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                }
            }
        }
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            ) {

                // Search Row - Only show if lyrics are not displayed
                if (lyrics == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.padding(end = 8.dp)){
                            HomeRectangleButton(onClick = { scope.launch { drawerState.open() } })
                        }

                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = {
                                Text("Search",
                                color = MaterialTheme.colorScheme.primaryContainer
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primaryContainer
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedTextColor = MaterialTheme.colorScheme.primaryContainer,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            trailingIcon = {
                                FilledIconButton (
                                    onClick = {
                                        scope.launch {
                                            isLoading = true
                                            errorMessage = null
                                            lyrics = null
                                            try {
                                                searchResults = repository.searchSongs(query)
                                            } catch (e: Exception) {
                                                errorMessage = e.message
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.size(63.dp).offset(x = -1.dp),
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                errorMessage?.let {
                    Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
                }

                if (lyrics != null) {
                    BackHandler {
                        lyrics = null
                    }
                    LyricsDisplay(songData = lyrics!!, modifier = Modifier.weight(1f))

                } else if (searchResults.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(searchResults) { song ->
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
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                } else if (!isLoading && errorMessage == null) {
                    Text(text = "Search for a song to see lyrics", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(song: GeniusSong, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = song.title, style = MaterialTheme.typography.bodySmall)
            Text(
                text = song.primary_artist.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
