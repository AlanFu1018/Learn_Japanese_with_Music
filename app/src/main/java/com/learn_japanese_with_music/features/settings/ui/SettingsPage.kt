package com.learn_japanese_with_music.features.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learn_japanese_with_music.core.components.HomeRectangleButton
import com.learn_japanese_with_music.core.data.SettingsManager
import com.worksap.nlp.sudachi.Tokenizer

@Composable
fun SettingsPage(
    settingsManager: SettingsManager,
    currentMode: Tokenizer.SplitMode,
    onModeChange: (Tokenizer.SplitMode) -> Unit,
    onMenuClick: () -> Unit
) {
    var geniusToken by remember { mutableStateOf(settingsManager.geniusApiToken) }
    var geminiKey by remember { mutableStateOf(settingsManager.geminiApiKey) }
    var geminiModel by remember { mutableStateOf(settingsManager.geminiModel) }
    val modes = listOf(Tokenizer.SplitMode.A, Tokenizer.SplitMode.B, Tokenizer.SplitMode.C)
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeRectangleButton(onClick = {
                    focusManager.clearFocus()
                    onMenuClick()
                })
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // API Key Section
            Text(
                text = "Genius API Access Token",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = geniusToken,
                onValueChange = {
                    geniusToken = it
                    settingsManager.geniusApiToken = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your Genius API token", style = MaterialTheme.typography.labelMedium) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gemini API Key Section
            Text(
                text = "Gemini API Key (for analysis)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = geminiKey,
                onValueChange = {
                    geminiKey = it
                    settingsManager.geminiApiKey = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your Gemini API key", style = MaterialTheme.typography.labelMedium) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gemini Model Section
            Text(
                text = "Gemini Model Name",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = geminiModel,
                onValueChange = {
                    geminiModel = it
                    settingsManager.geminiModel = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., gemini-1.5-pro", style = MaterialTheme.typography.labelMedium) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Sudachi Mode Section
            Text(
                text = "Sudachi Syntax Analyzer Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))

            modes.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentMode == mode,
                        onClick = {
                            onModeChange(mode)
                        }
                    )

                    if(mode.name.equals("A")){
                        Column() {
                            Text(text = "Mode ${mode.name} ",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "cut lyrics into the smallest unit as possible",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }else if(mode.name.equals("B")){
                        Column() {
                            Text(text = "Mode ${mode.name} ",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "word unit length between A, C Mode",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }else{
                        Column() {
                            Text(text = "Mode ${mode.name} ",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "cut lyrics in the biggest unit as possible",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }

}
