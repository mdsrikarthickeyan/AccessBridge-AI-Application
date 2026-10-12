package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.VerificationRecord
import com.example.model.VerificationStatus
import com.example.model.VerifierRole

@Entity(tableName = "verification_records")
data class VerificationRecordEntity(
    @PrimaryKey val id: String,
    val facilityId: String,
    val verifierName: String,
    val verifierRoleName: String,
    val verificationDate: String,
    val auditMethod: String,
    val evidenceNotes: String,
    val verifiedFeaturesJson: String, // serialized with delimiter |||
    val statusAssignedName: String,
    val isOfficialAudit: Boolean
) {
    fun toModel(): VerificationRecord {
        val role = VerifierRole.entries.find { it.name == verifierRoleName } ?: VerifierRole.CERTIFIED_AUDITOR
        val status = VerificationStatus.entries.find { it.name == statusAssignedName } ?: VerificationStatus.VERIFIED
        val features = if (verifiedFeaturesJson.isBlank()) emptyList() else verifiedFeaturesJson.split("|||")
        return VerificationRecord(
            id = id,
            facilityId = facilityId,
            verifierName = verifierName,
            verifierRole = role,
            verificationDate = verificationDate,
            auditMethod = auditMethod,
            evidenceNotes = evidenceNotes,
            verifiedFeatures = features,
            statusAssigned = status,
            isOfficialAudit = isOfficialAudit
        )
    }

    companion object {
        fun fromModel(model: VerificationRecord): VerificationRecordEntity {
            return VerificationRecordEntity(
                id = model.id,
                facilityId = model.facilityId,
                verifierName = model.verifierName,
                verifierRoleName = model.verifierRole.name,
                verificationDate = model.verificationDate,
                auditMethod = model.auditMethod,
                evidenceNotes = model.evidenceNotes,
                verifiedFeaturesJson = model.verifiedFeatures.joinToString("|||"),
                statusAssignedName = model.statusAssigned.name,
                isOfficialAudit = model.isOfficialAudit
            )
        }
    }
}
