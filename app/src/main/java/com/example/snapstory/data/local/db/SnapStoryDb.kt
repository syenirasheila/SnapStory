package com.example.snapstory.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.snapstory.data.local.db.dao.RemoteKeyDao
import com.example.snapstory.data.local.db.dao.StoryDao
import com.example.snapstory.data.local.db.entity.RemoteKey
import com.example.snapstory.data.remote.model.StoryItem

@Database(
    entities = [StoryItem::class, RemoteKey::class],
    version = 2,
    exportSchema = false
)
abstract class SnapStoryDb : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: SnapStoryDb? = null

        @JvmStatic
        fun getDb(context: Context): SnapStoryDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SnapStoryDb::class.java, "snapStory_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it }
            }
        }
    }
}