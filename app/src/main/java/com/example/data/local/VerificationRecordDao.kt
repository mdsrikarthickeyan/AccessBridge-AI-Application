package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationRecordDao {
    @Query("SELECT * FROM verification_records WHERE facilityId = :facilityId")
    fun getRecordsForFacility(facilityId: String): Flow<List<VerificationRecordEntity>>

    @Query("SELECT * FROM verification_records ORDER BY verificationDate DESC")
    fun getAllRecords(): Flow<List<VerificationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: VerificationRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<VerificationRecordEntity>)

    @Query("SELECT COUNT(*) FROM verification_records")
    suspend fun getCount(): Int
}
