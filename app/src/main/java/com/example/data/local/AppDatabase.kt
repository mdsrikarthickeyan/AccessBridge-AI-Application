package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BarrierReportEntity::class,
        ReportStatusHistoryEntity::class,
        FacilityEntity::class,
        VerificationRecordEntity::class,
        SavedNoticeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barrierReportDao(): BarrierReportDao
    abstract fun reportStatusHistoryDao(): ReportStatusHistoryDao
    abstract fun facilityDao(): FacilityDao
    abstract fun verificationRecordDao(): VerificationRecordDao
    abstract fun savedNoticeDao(): SavedNoticeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "accessbridge_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
