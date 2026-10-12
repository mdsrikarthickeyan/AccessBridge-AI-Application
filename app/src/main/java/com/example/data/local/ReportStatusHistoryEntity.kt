package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BarrierStatus
import com.example.model.ReportStatusHistoryItem

@Entity(tableName = "report_status_history")
data class ReportStatusHistoryEntity(
    @PrimaryKey val id: String,
    val reportId: String,
    val fromStatusName: String?,
    val toStatusName: String,
    val changedBy: String,
    val changedRole: String,
    val timestamp: String,
    val reviewNote: String,
    val actionTaken: String
) {
    fun toModel(): ReportStatusHistoryItem {
        val fromStatus = fromStatusName?.let { name ->
            BarrierStatus.entries.find { it.name == name }
        }
        val toStatus = BarrierStatus.entries.find { it.name == toStatusName } ?: BarrierStatus.RECEIVED
        return ReportStatusHistoryItem(
            id = id,
            reportId = reportId,
            fromStatus = fromStatus,
            toStatus = toStatus,
            changedBy = changedBy,
            changedRole = changedRole,
            timestamp = timestamp,
            reviewNote = reviewNote,
            actionTaken = actionTaken
        )
    }

    companion object {
        fun fromModel(model: ReportStatusHistoryItem): ReportStatusHistoryEntity {
            return ReportStatusHistoryEntity(
                id = model.id,
                reportId = model.reportId,
                fromStatusName = model.fromStatus?.name,
                toStatusName = model.toStatus.name,
                changedBy = model.changedBy,
                changedRole = model.changedRole,
                timestamp = model.timestamp,
                reviewNote = model.reviewNote,
                actionTaken = model.actionTaken
            )
        }
    }
}
