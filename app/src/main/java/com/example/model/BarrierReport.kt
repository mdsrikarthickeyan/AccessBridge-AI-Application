package com.example.model

enum class BarrierCategory(val label: String) {
    BROKEN_RAMP("Broken or Steep Ramp"),
    BLOCKED_TACTILE("Blocked Tactile Paving"),
    ELEVATOR_OUT("Out of Order Elevator / Lift"),
    INACCESSIBLE_ENTRANCE("Inaccessible Steps / Narrow Gate"),
    MISSING_SIGN_ASSIST("No Sign Language Support"),
    ACCESSIBLE_TOILET_LOCKED("Accessible Toilet Locked / Damaged"),
    SENSORY_HAZARD("Sensory / Loud Alarm Hazard")
}

enum class BarrierSeverity(val label: String, val levelOrder: Int) {
    LOW("Minor Inconvenience", 1),
    MEDIUM("Noticeable Obstacle", 2),
    HIGH("Blocks Access Completely", 3),
    URGENT("Immediate Safety Risk", 4)
}

enum class BarrierStatus(val label: String, val description: String, val stepIndex: Int) {
    RECEIVED("Received", "Report logged in system; awaiting initial intake", 0),
    WAITING_FOR_REVIEW("Waiting for review", "Queued for accessibility coordinator evaluation", 1),
    BEING_CHECKED("Being checked", "Physical site inspection or facility contact initiated", 2),
    RESOLVED("Resolved", "Barrier removed or accessible accommodation completed", 3),
    UNABLE_TO_VERIFY("Unable to verify", "Insufficient details or condition could not be confirmed", 4)
}

data class BarrierReport(
    val id: String,
    val title: String,
    val category: BarrierCategory,
    val locationName: String,
    val city: String,
    val specificLocationDetails: String,
    val severity: BarrierSeverity,
    val status: BarrierStatus = BarrierStatus.RECEIVED,
    val dateReported: String,
    val timestamp: Long = System.currentTimeMillis(),
    val statusNotes: String = "Report submitted by visitor; initial review pending.",
    val resolutionEstimate: String? = null,
    val facilityId: String? = null, // Facility reference where known
    val optionalImageUrl: String? = null,
    val contactEmailOrPhone: String? = null, // Optional for user privacy
    val isUserSubmitted: Boolean = false, // Allows withdrawing one's own report
    val reporterUserId: String? = null,
    val hasConflictWithOfficialAudit: Boolean = false
)
