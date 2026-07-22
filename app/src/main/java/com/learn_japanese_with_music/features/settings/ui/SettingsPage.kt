package com.learn_japanese_with_music.features.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learn_japanese_with_music.core.components.HomeRectangleButton
import com.learn_japanese_with_music.core.data.SettingsManager
import com.worksap.nlp.sudachi.Tokenizer

@Composable
fun SettingsPage(
    settingsManager: SettingsManager,
    onMenuClick: () -> Unit
) {
    var geniusToken by remember { mutableStateOf(settingsManager.geniusApiToken) }
    var selectedMode by remember { mutableStateOf(settingsManager.sudachiSplitMode) }
    val modes = listOf(Tokenizer.SplitMode.A, Tokenizer.SplitMode.B, Tokenizer.SplitMode.C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeRectangleButton(onClick = onMenuClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // API Key Section
        Text(
            text = "Genius API Access Token",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = geniusToken,
            onValueChange = { 
                geniusToken = it
                settingsManager.geniusApiToken = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your Genius API token") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sudachi Mode Section
        Text(
            text = "Sudachi Syntax Analyzer Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        modes.forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMode == mode,
                    onClick = { 
                        selectedMode = mode
                        settingsManager.sudachiSplitMode = mode
                    }
                )
                Text(
                    text = "${mode.name} Mode",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
