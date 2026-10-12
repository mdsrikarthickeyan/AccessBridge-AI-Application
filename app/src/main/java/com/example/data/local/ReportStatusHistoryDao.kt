package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportStatusHistoryDao {
    @Query("SELECT * FROM report_status_history WHERE reportId = :reportId ORDER BY rowid ASC")
    fun getHistoryForReport(reportId: String): Flow<List<ReportStatusHistoryEntity>>

    @Query("SELECT * FROM report_status_history ORDER BY rowid DESC")
    fun getAllHistory(): Flow<List<ReportStatusHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: ReportStatusHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ReportStatusHistoryEntity>)

    @Query("DELETE FROM report_status_history WHERE reportId = :reportId")
    suspend fun deleteHistoryForReport(reportId: String)
}
