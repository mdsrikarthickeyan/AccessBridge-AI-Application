package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.NoticeCategory
import com.example.model.NoticeItem

@Entity(tableName = "saved_notices")
data class SavedNoticeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryName: String,
    val originalText: String,
    val plainSummary: String,
    val keyPointsJson: String, // comma or pipe separated
    val actionRequired: String,
    val deadline: String?,
    val emergencyContact: String?,
    val isVerified: Boolean,
    val dateAdded: String
) {
    fun toModel(): NoticeItem {
        val category = NoticeCategory.entries.find { it.name == categoryName } ?: NoticeCategory.PUBLIC_NOTICE
        val points = keyPointsJson.split("|||").filter { it.isNotBlank() }
        return NoticeItem(
            id = id,
            title = title,
            category = category,
            originalText = originalText,
            plainSummary = plainSummary,
            keyPoints = points,
            actionRequired = actionRequired,
            deadline = deadline,
            emergencyContact = emergencyContact,
            isVerified = isVerified,
            dateAdded = dateAdded,
            isSaved = true
        )
    }

    companion object {
        fun fromModel(model: NoticeItem): SavedNoticeEntity {
            return SavedNoticeEntity(
                id = model.id,
                title = model.title,
                categoryName = model.category.name,
                originalText = model.originalText,
                plainSummary = model.plainSummary,
                keyPointsJson = model.keyPoints.joinToString("|||"),
                actionRequired = model.actionRequired,
                deadline = model.deadline,
                emergencyContact = model.emergencyContact,
                isVerified = model.isVerified,
                dateAdded = model.dateAdded
            )
        }
    }
}
