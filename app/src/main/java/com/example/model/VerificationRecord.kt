package com.example.model

enum class VerifierRole(val label: String) {
    CERTIFIED_AUDITOR("Certified Access Auditor"),
    CAMPUS_COORDINATOR("Campus Accessibility Coordinator"),
    FACILITY_ENGINEER("Public Works Engineer"),
    COMMUNITY_MONITOR("Trained Community Volunteer")
}

data class VerificationRecord(
    val id: String,
    val facilityId: String,
    val verifierName: String,
    val verifierRole: VerifierRole,
    val verificationDate: String,
    val auditMethod: String, // e.g. "On-Site Physical Audit with Incline Gauge", "Official Architectural Verification"
    val evidenceNotes: String,
    val verifiedFeatures: List<String>,
    val statusAssigned: VerificationStatus,
    val isOfficialAudit: Boolean = true
)
