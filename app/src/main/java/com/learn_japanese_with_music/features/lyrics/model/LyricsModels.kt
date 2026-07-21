package com.learn_japanese_with_music.features.lyrics.model

/**
 * API Models for Genius
 */
data class GeniusResponse(
    val response: GeniusSearchResult
)

data class GeniusSearchResult(
    val hits: List<GeniusHit>
)

data class GeniusHit(
    val result: GeniusSong
)

data class GeniusSong(
    val id: Int,
    val title: String,
    val full_title: String,
    val url: String,
    val song_art_image_thumbnail_url: String,
    val primary_artist: GeniusArtist
)

data class GeniusArtist(
    val name: String
)

/**
 * Domain Models for Lyrics Display
 */

data class LyricSegment(
    val text: String,
    val reading: String = ""
)

data class LyricLine(
    val segments: List<LyricSegment>
)

data class SongData(
    val title: String,
    val artist: String,
    val cover: String,
    val lyrics: List<LyricLine>
)
