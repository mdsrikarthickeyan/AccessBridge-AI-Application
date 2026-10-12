package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val localName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    TELUGU("te", "Telugu", "తెలుగు"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ"),
    BENGALI("bn", "Bengali", "বাংলা"),
    MARATHI("mr", "Marathi", "मराठी")
}

enum class TtsVoiceTone(val label: String, val pitch: Float) {
    NATURAL("Natural (Standard)", 1.0f),
    DEEP("Deeper Tone", 0.85f),
    HIGH("Higher Tone", 1.2f)
}

data class AccessibilityPreferences(
    // Visual & Appearance
    val highContrast: Boolean = false,
    val fontScale: Float = 1.0f, // 1.0f, 1.15f, 1.3f, 1.5f
    val dyslexiaSpacing: Boolean = false,
    val reducedMotion: Boolean = false,
    val keyboardFriendlyNavigation: Boolean = false,
    val reduceUnnecessarySteps: Boolean = false,

    // Language & Spoken Audio
    val preferredLanguage: AppLanguage = AppLanguage.ENGLISH,
    val spokenOutputEnabled: Boolean = true,
    val speechRate: Float = 1.0f, // 0.75f, 1.0f, 1.25f
    val voiceTone: TtsVoiceTone = TtsVoiceTone.NATURAL,
    val hapticFeedback: Boolean = true,
    val highContrastFlashcards: Boolean = true,

    // My Access Passport (Facility Requirements for ranking places)
    val requireStepFree: Boolean = false,
    val requireRamp: Boolean = false,
    val requireLift: Boolean = false,
    val requireAccessibleToilet: Boolean = false,
    val requireWheelchairRoute: Boolean = false,
    val requireTactileGuidance: Boolean = false,
    val requireBrailleSignage: Boolean = false,
    val requireAudioAssistance: Boolean = false,
    val requireVisualAnnouncements: Boolean = false,
    val requireAccessibleParking: Boolean = false
) {
    fun hasAnyFacilityRequirement(): Boolean {
        return requireStepFree || requireRamp || requireLift || requireAccessibleToilet ||
                requireWheelchairRoute || requireTactileGuidance || requireBrailleSignage ||
                requireAudioAssistance || requireVisualAnnouncements || requireAccessibleParking
    }

    fun activeRequirementCount(): Int {
        var count = 0
        if (requireStepFree) count++
        if (requireRamp) count++
        if (requireLift) count++
        if (requireAccessibleToilet) count++
        if (requireWheelchairRoute) count++
        if (requireTactileGuidance) count++
        if (requireBrailleSignage) count++
        if (requireAudioAssistance) count++
        if (requireVisualAnnouncements) count++
        if (requireAccessibleParking) count++
        return count
    }

    fun calculatePlaceMatchScore(place: AccessiblePlace): Int {
        var score = 0
        if (requireStepFree && place.stepFreeEntrance) score += 4
        if (requireRamp && place.wheelchairRamp) score += 4
        if (requireLift && place.liftAvailable) score += 4
        if (requireAccessibleToilet && place.accessibleToilet) score += 3
        if (requireWheelchairRoute && place.wheelchairAccessibleRoute) score += 3
        if (requireTactileGuidance && place.tactilePaving) score += 3
        if (requireBrailleSignage && place.brailleSignage) score += 3
        if (requireAudioAssistance && place.audioAssistance) score += 3
        if (requireVisualAnnouncements && place.visualAnnouncements) score += 3
        if (requireAccessibleParking && place.accessibleParking) score += 3
        if (place.verificationStatus == VerificationStatus.VERIFIED) score += 1
        return score
    }

    fun getActiveFacilityLabels(): List<String> {
        val list = mutableListOf<String>()
        if (requireStepFree) list.add("Step-free entrance")
        if (requireRamp) list.add("Ramp")
        if (requireLift) list.add("Lift")
        if (requireAccessibleToilet) list.add("Accessible toilet")
        if (requireWheelchairRoute) list.add("Wheelchair route")
        if (requireTactileGuidance) list.add("Tactile guidance")
        if (requireBrailleSignage) list.add("Braille signs")
        if (requireAudioAssistance) list.add("Audio assistance")
        if (requireVisualAnnouncements) list.add("Visual announcements")
        if (requireAccessibleParking) list.add("Accessible parking")
        return list
    }
}
