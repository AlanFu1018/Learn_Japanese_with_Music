package com.learn_japanese_with_music.core.network

import com.learn_japanese_with_music.BuildConfig
import com.learn_japanese_with_music.features.lyrics.api.GeniusService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.genius.com/"
    
    // 提供一個可變的 Provider，讓攔截器能讀取到最新的 Token
    var tokenProvider: () -> String = { BuildConfig.GENIUS_API_TOKEN }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenProvider()}")
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
