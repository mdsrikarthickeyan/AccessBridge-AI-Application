package com.example.model

enum class PlaceType(val label: String) {
    ALL("All Places"),
    METRO("Metro & Rail"),
    HOSPITAL("Hospital & Healthcare"),
    COLLEGE("College & University"),
    LIBRARY("Library & Culture"),
    GOVERNMENT("Public Office")
}

enum class VerificationStatus(
    val label: String,
    val badgeText: String,
    val description: String
) {
    VERIFIED(
        "Verified",
        "Verified",
        "Audited and verified by certified accessibility assessors."
    ),
    REPORTED_BY_VISITOR(
        "Reported by a visitor",
        "Visitor Report",
        "Shared by a recent visitor with photos or notes; awaiting full audit."
    ),
    NOT_YET_CHECKED(
        "Not yet checked",
        "Not Checked",
        "Facility listed from public records; accessibility has not been verified yet."
    ),
    OUTDATED(
        "Information may be outdated",
        "May Be Outdated",
        "Audited over 12 months ago; facilities or maintenance conditions may have changed."
    )
}

data class AccessiblePlace(
    val id: String,
    val name: String,
    val type: PlaceType,
    val city: String,
    val address: String,
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val lastVerifiedDate: String = "September 2026",
    val informationSource: String = "Official Campus Accessibility Audit",
    val openingHours: String? = "08:00 AM - 08:00 PM",
    val entranceDetails: String = "Step-free ramp with 1:12 gradient at main entrance gate",
    // Documented Accessibility Features
    val stepFreeEntrance: Boolean = true,
    val wheelchairRamp: Boolean = true,
    val liftAvailable: Boolean = true,
    val accessibleToilet: Boolean = true,
    val wheelchairAccessibleRoute: Boolean = true,
    val tactilePaving: Boolean = false,
    val brailleSignage: Boolean = false,
    val audioAssistance: Boolean = false,
    val visualAnnouncements: Boolean = false,
    val accessibleParking: Boolean = false,
    val signLanguageAssistance: Boolean = false,
    val otherFacilities: List<String> = emptyList(),
    // Assistance Contact & Route
    val assistanceDeskPhone: String? = null,
    val description: String,
    val accessibilityNotes: String,
    val nearestTransit: String,
    val mapLink: String? = null,
    val hasConflict: Boolean = false,
    val conflictDescription: String? = null,
    val communityReportsCount: Int = 0
) {
    // Alias property for compatibility
    val audioAnnouncements: Boolean
        get() = audioAssistance || visualAnnouncements

    fun matchScoreForPreferences(
        needsRamp: Boolean,
        needsTactile: Boolean,
        needsLift: Boolean,
        needsToilet: Boolean,
        needsBraille: Boolean,
        needsAudio: Boolean,
        needsParking: Boolean
    ): Int {
        var score = 0
        if (needsRamp && (wheelchairRamp || stepFreeEntrance)) score += 3
        if (needsTactile && tactilePaving) score += 3
        if (needsLift && liftAvailable) score += 3
        if (needsToilet && accessibleToilet) score += 2
        if (needsBraille && brailleSignage) score += 2
        if (needsAudio && (audioAssistance || visualAnnouncements)) score += 2
        if (needsParking && accessibleParking) score += 2
        if (verificationStatus == VerificationStatus.VERIFIED) score += 1
        return score
    }
}
