package com.example.model

enum class NoticeCategory(val title: String) {
    PUBLIC_NOTICE("Public Notice"),
    HOSPITAL("Hospital & Clinic"),
    TRANSIT("Transit & Station"),
    COLLEGE("College & School"),
    GOVERNMENT_FORM("Government Form & ID"),
    UTILITY("Utility & Bill")
}

data class NoticeItem(
    val id: String,
    val title: String,
    val category: NoticeCategory,
    val originalText: String,
    val plainSummary: String,
    val keyPoints: List<String>,
    val actionRequired: String,
    val deadline: String? = null,
    val emergencyContact: String? = null,
    val isVerified: Boolean = true,
    val dateAdded: String,
    val isSaved: Boolean = false
)
