package com.learn_japanese_with_music.features.lyrics.api

import com.learn_japanese_with_music.features.lyrics.model.GeniusResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeniusService {
    @GET("search")
    suspend fun searchSongs(
        @Query("q") query: String
    ): GeniusResponse
}
