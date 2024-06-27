package com.example.snapstory.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snapstory.data.remote.model.StoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(quote: List<StoryItem>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, StoryItem>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}