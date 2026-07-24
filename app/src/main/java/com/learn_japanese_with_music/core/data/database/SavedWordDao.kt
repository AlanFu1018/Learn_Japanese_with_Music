package com.learn_japanese_with_music.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedWordDao {
    @Query("SELECT * FROM SavedWord WHERE word = :word AND contextLine = :contextLine AND splitMode = :splitMode")
    suspend fun getSavedWord(word: String, contextLine: String, splitMode: String): SavedWord?

    @Query("SELECT * FROM SavedWord ORDER BY timestamp DESC")
    suspend fun getAllSavedWords(): List<SavedWord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedWord(savedWord: SavedWord)

    @Query("UPDATE SavedWord SET notes = :notes WHERE word = :word AND contextLine = :contextLine AND splitMode = :splitMode")
    suspend fun updateNotes(word: String, contextLine: String, splitMode: String, notes: String)

    @Delete
    suspend fun deleteSavedWord(savedWord: SavedWord)
    
    @Query("DELETE FROM SavedWord WHERE word = :word AND contextLine = :contextLine AND splitMode = :splitMode")
    suspend fun deleteSavedWordById(word: String, contextLine: String, splitMode: String)
}
