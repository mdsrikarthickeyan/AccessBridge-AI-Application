package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BarrierCategory
import com.example.model.BarrierReport
import com.example.model.BarrierSeverity
import com.example.model.BarrierStatus

@Entity(tableName = "barrier_reports")
data class BarrierReportEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryName: String,
    val locationName: String,
    val city: String,
    val specificLocationDetails: String,
    val severityName: String,
    val statusName: String,
    val dateReported: String,
    val timestamp: Long,
    val statusNotes: String,
    val resolutionEstimate: String?,
    val facilityId: String?,
    val optionalImageUrl: String?,
    val contactEmailOrPhone: String?,
    val isUserSubmitted: Boolean,
    val reporterUserId: String?,
    val hasConflictWithOfficialAudit: Boolean
) {
    fun toModel(): BarrierReport {
        val category = BarrierCategory.entries.find { it.name == categoryName } ?: BarrierCategory.BROKEN_RAMP
        val severity = BarrierSeverity.entries.find { it.name == severityName } ?: BarrierSeverity.MEDIUM
        val status = BarrierStatus.entries.find { it.name == statusName } ?: BarrierStatus.RECEIVED
        return BarrierReport(
            id = id,
            title = title,
            category = category,
            locationName = locationName,
            city = city,
            specificLocationDetails = specificLocationDetails,
            severity = severity,
            status = status,
            dateReported = dateReported,
            timestamp = timestamp,
            statusNotes = statusNotes,
            resolutionEstimate = resolutionEstimate,
            facilityId = facilityId,
            optionalImageUrl = optionalImageUrl,
            contactEmailOrPhone = contactEmailOrPhone,
            isUserSubmitted = isUserSubmitted,
            reporterUserId = reporterUserId,
            hasConflictWithOfficialAudit = hasConflictWithOfficialAudit
        )
    }

    companion object {
        fun fromModel(model: BarrierReport): BarrierReportEntity {
            return BarrierReportEntity(
                id = model.id,
                title = model.title,
                categoryName = model.category.name,
                locationName = model.locationName,
                city = model.city,
                specificLocationDetails = model.specificLocationDetails,
                severityName = model.severity.name,
                statusName = model.status.name,
                dateReported = model.dateReported,
                timestamp = model.timestamp,
                statusNotes = model.statusNotes,
                resolutionEstimate = model.resolutionEstimate,
                facilityId = model.facilityId,
                optionalImageUrl = model.optionalImageUrl,
                contactEmailOrPhone = model.contactEmailOrPhone,
                isUserSubmitted = model.isUserSubmitted,
                reporterUserId = model.reporterUserId,
                hasConflictWithOfficialAudit = model.hasConflictWithOfficialAudit
            )
        }
    }
}
