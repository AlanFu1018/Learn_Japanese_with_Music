package com.learn_japanese_with_music.lyrics_display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn_japanese_with_music.ui.theme.appTheme

/**
 * 代表一段歌詞及其讀音的資料結構
 * @param text 主要顯示的日文（漢字或假名）
 * @param reading 顯示在下方的讀音（片假名或平假名）
 */
data class LyricSegment(
    val text: String,
    val reading: String = ""
)

/**
 * 代表一行歌詞
 */
data class LyricLine(
    val segments: List<LyricSegment>
)

/**
 * lyrics ui
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LyricLineDisplay(
    line: LyricLine,
    modifier: Modifier = Modifier
) {
    Column {
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            line.segments.forEach { segment ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    // 主要日文文字
                    Text(
                        text = segment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // 下方的片假名讀音
                    if (segment.reading.isNotEmpty()) {
                        Text(
                            text = segment.reading,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(20.dp))
    }

}

@Composable
fun LyricsDisplay(
    songData: SongData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 14.dp, start = 14.dp, end = 14.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = songData.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = songData.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }

            items(songData.lyrics) { line ->
                LyricLineDisplay(line = line)
            }
        }
    }
}


/**
 * for the ui test
 */
@Preview(showBackground = true)
@Composable
fun LyricPreview() {
    val sampleLyrics = listOf(
        LyricLine(
            segments = listOf(
                LyricSegment("君", "キミ"),
                LyricSegment("の"),
                LyricSegment("名", "ナ"),
                LyricSegment("は"),
                LyricSegment("。")
            )
        ),
        LyricLine(
            segments = listOf(
                LyricSegment("前", "ゼン"),
                LyricSegment("前", "ゼン"),
                LyricSegment("前", "ゼン"),
                LyricSegment("世", "セ"),
                LyricSegment("から")
            )
        )
    )

    val sampleSongData = SongData(
        title = "前前前世",
        artist = "RADWIMPS",
        lyrics = sampleLyrics
    )

    appTheme {
        Column() {
            LyricsDisplay(songData = sampleSongData)
        }

    }
}
