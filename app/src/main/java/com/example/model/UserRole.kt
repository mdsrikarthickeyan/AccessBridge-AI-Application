package com.example.model

enum class UserRole(
    val label: String,
    val roleTitle: String,
    val description: String
) {
    PUBLIC_USER(
        label = "Public User",
        roleTitle = "Community Visitor",
        description = "Can search facilities, submit barrier reports, view own submission tracking, and withdraw own reports."
    ),
    ACCESSIBILITY_REVIEWER(
        label = "Field Reviewer",
        roleTitle = "Certified Accessibility Auditor",
        description = "Can perform physical inspections, update report statuses, append administrative notes, and flag facility conflicts."
    ),
    SYSTEM_ADMIN(
        label = "System Administrator",
        roleTitle = "Platform Administrator",
        description = "Full control over verification records, database mode, status audit trails, and role management."
    );

    val canReviewReports: Boolean
        get() = this == ACCESSIBILITY_REVIEWER || this == SYSTEM_ADMIN

    val canEditVerifications: Boolean
        get() = this == ACCESSIBILITY_REVIEWER || this == SYSTEM_ADMIN

    val canFlagConflicts: Boolean
        get() = this == ACCESSIBILITY_REVIEWER || this == SYSTEM_ADMIN

    val canDeleteAnyReport: Boolean
        get() = this == SYSTEM_ADMIN
}
