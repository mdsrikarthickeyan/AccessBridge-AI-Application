package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AccessiblePlace
import com.example.model.PlaceType
import com.example.model.VerificationStatus

@Entity(tableName = "facilities")
data class FacilityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val typeName: String,
    val city: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val verificationStatusName: String,
    val lastVerifiedDate: String,
    val informationSource: String,
    val openingHours: String?,
    val entranceDetails: String,
    val stepFreeEntrance: Boolean,
    val wheelchairRamp: Boolean,
    val liftAvailable: Boolean,
    val accessibleToilet: Boolean,
    val wheelchairAccessibleRoute: Boolean,
    val tactilePaving: Boolean,
    val brailleSignage: Boolean,
    val audioAssistance: Boolean,
    val visualAnnouncements: Boolean,
    val accessibleParking: Boolean,
    val signLanguageAssistance: Boolean,
    val otherFacilitiesJson: String,
    val assistanceDeskPhone: String?,
    val description: String,
    val accessibilityNotes: String,
    val nearestTransit: String,
    val mapLink: String?,
    val hasConflict: Boolean,
    val conflictDescription: String?,
    val communityReportsCount: Int
) {
    fun toModel(): AccessiblePlace {
        val type = PlaceType.entries.find { it.name == typeName } ?: PlaceType.ALL
        val status = VerificationStatus.entries.find { it.name == verificationStatusName } ?: VerificationStatus.VERIFIED
        val others = if (otherFacilitiesJson.isBlank()) emptyList() else otherFacilitiesJson.split("|||")
        return AccessiblePlace(
            id = id,
            name = name,
            type = type,
            city = city,
            address = address,
            latitude = latitude,
            longitude = longitude,
            verificationStatus = status,
            lastVerifiedDate = lastVerifiedDate,
            informationSource = informationSource,
            openingHours = openingHours,
            entranceDetails = entranceDetails,
            stepFreeEntrance = stepFreeEntrance,
            wheelchairRamp = wheelchairRamp,
            liftAvailable = liftAvailable,
            accessibleToilet = accessibleToilet,
            wheelchairAccessibleRoute = wheelchairAccessibleRoute,
            tactilePaving = tactilePaving,
            brailleSignage = brailleSignage,
            audioAssistance = audioAssistance,
            visualAnnouncements = visualAnnouncements,
            accessibleParking = accessibleParking,
            signLanguageAssistance = signLanguageAssistance,
            otherFacilities = others,
            assistanceDeskPhone = assistanceDeskPhone,
            description = description,
            accessibilityNotes = accessibilityNotes,
            nearestTransit = nearestTransit,
            mapLink = mapLink,
            hasConflict = hasConflict,
            conflictDescription = conflictDescription,
            communityReportsCount = communityReportsCount
        )
    }

    companion object {
        fun fromModel(model: AccessiblePlace): FacilityEntity {
            return FacilityEntity(
                id = model.id,
                name = model.name,
                typeName = model.type.name,
                city = model.city,
                address = model.address,
                latitude = model.latitude,
                longitude = model.longitude,
                verificationStatusName = model.verificationStatus.name,
                lastVerifiedDate = model.lastVerifiedDate,
                informationSource = model.informationSource,
                openingHours = model.openingHours,
                entranceDetails = model.entranceDetails,
                stepFreeEntrance = model.stepFreeEntrance,
                wheelchairRamp = model.wheelchairRamp,
                liftAvailable = model.liftAvailable,
                accessibleToilet = model.accessibleToilet,
                wheelchairAccessibleRoute = model.wheelchairAccessibleRoute,
                tactilePaving = model.tactilePaving,
                brailleSignage = model.brailleSignage,
                audioAssistance = model.audioAssistance,
                visualAnnouncements = model.visualAnnouncements,
                accessibleParking = model.accessibleParking,
                signLanguageAssistance = model.signLanguageAssistance,
                otherFacilitiesJson = model.otherFacilities.joinToString("|||"),
                assistanceDeskPhone = model.assistanceDeskPhone,
                description = model.description,
                accessibilityNotes = model.accessibilityNotes,
                nearestTransit = model.nearestTransit,
                mapLink = model.mapLink,
                hasConflict = model.hasConflict,
                conflictDescription = model.conflictDescription,
                communityReportsCount = model.communityReportsCount
            )
        }
    }
}
