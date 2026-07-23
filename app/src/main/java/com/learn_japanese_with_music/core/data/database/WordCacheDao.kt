package com.learn_japanese_with_music.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordCacheDao {
    @Query("SELECT * FROM WordCache WHERE word = :word AND contextLine = :contextLine AND splitMode = :splitMode")
    suspend fun getWordCache(word: String, contextLine: String, splitMode: String): WordCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordCache(wordCache: WordCache)

    @Query("DELETE FROM WordCache WHERE timestamp < :expiryTime")
    suspend fun deleteOldCaches(expiryTime: Long)
}
