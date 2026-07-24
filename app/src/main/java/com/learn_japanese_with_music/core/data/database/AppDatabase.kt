package com.learn_japanese_with_music.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WordCache::class, SavedWord::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordCacheDao(): WordCacheDao
    abstract fun savedWordDao(): SavedWordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
