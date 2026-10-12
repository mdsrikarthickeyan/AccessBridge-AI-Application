package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNoticeDao {
    @Query("SELECT * FROM saved_notices ORDER BY dateAdded DESC")
    fun getAllSavedNotices(): Flow<List<SavedNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: SavedNoticeEntity)

    @Delete
    suspend fun deleteNotice(notice: SavedNoticeEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_notices WHERE id = :id)")
    suspend fun isNoticeSaved(id: String): Boolean
}
