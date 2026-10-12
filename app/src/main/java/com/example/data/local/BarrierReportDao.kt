package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BarrierReportDao {
    @Query("SELECT * FROM barrier_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<BarrierReportEntity>>

    @Query("SELECT * FROM barrier_reports WHERE id = :reportId LIMIT 1")
    suspend fun getReportById(reportId: String): BarrierReportEntity?

    @Query("SELECT * FROM barrier_reports WHERE facilityId = :facilityId ORDER BY timestamp DESC")
    fun getReportsForFacility(facilityId: String): Flow<List<BarrierReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: BarrierReportEntity)

    @Update
    suspend fun updateReport(report: BarrierReportEntity)

    @Query("UPDATE barrier_reports SET statusName = :newStatus, statusNotes = :reviewNote WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, newStatus: String, reviewNote: String)

    @Delete
    suspend fun deleteReport(report: BarrierReportEntity)

    @Query("DELETE FROM barrier_reports WHERE id = :reportId")
    suspend fun deleteReportById(reportId: String)

    @Query("SELECT COUNT(*) FROM barrier_reports")
    suspend fun getCount(): Int
}
