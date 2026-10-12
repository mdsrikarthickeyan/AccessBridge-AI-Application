package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Query("SELECT * FROM facilities ORDER BY name ASC")
    fun getAllFacilities(): Flow<List<FacilityEntity>>

    @Query("SELECT * FROM facilities WHERE id = :id LIMIT 1")
    suspend fun getFacilityById(id: String): FacilityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacility(facility: FacilityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<FacilityEntity>)

    @Update
    suspend fun updateFacility(facility: FacilityEntity)

    @Query("UPDATE facilities SET hasConflict = :hasConflict, conflictDescription = :description WHERE id = :facilityId")
    suspend fun updateConflictStatus(facilityId: String, hasConflict: Boolean, description: String?)

    @Delete
    suspend fun deleteFacility(facility: FacilityEntity)

    @Query("SELECT COUNT(*) FROM facilities")
    suspend fun getCount(): Int
}
