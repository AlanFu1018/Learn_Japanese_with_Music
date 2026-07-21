package com.learn_japanese_with_music.lyrics_display

import com.learn_japanese_with_music.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeniusService {
    @GET("search")
    suspend fun searchSongs(
        @Query("q") query: String
    ): GeniusResponse
}

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
 * 完整歌曲資料，包含基本資訊與處理過的歌詞
 */
data class SongData(
    val title: String,
    val artist: String,
    val cover: String,
    val lyrics: List<LyricLine>
)

object RetrofitClient {
    private const val BASE_URL = "https://api.genius.com/"
    private const val ACCESS_TOKEN = BuildConfig.GENIUS_API_TOKEN

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val geniusService: GeniusService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeniusService::class.java)
    }
}
