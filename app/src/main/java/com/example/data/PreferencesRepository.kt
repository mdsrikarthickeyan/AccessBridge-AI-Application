package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AccessibilityPreferences
import com.example.model.AppLanguage
import com.example.model.TtsVoiceTone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("accessbridge_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<AccessibilityPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): AccessibilityPreferences {
        val highContrast = prefs.getBoolean("high_contrast", false)
        val fontScale = prefs.getFloat("font_scale", 1.0f)
        val dyslexiaSpacing = prefs.getBoolean("dyslexia_spacing", false)
        val reducedMotion = prefs.getBoolean("reduced_motion", false)
        val keyboardFriendly = prefs.getBoolean("keyboard_friendly", false)
        val reduceSteps = prefs.getBoolean("reduce_steps", false)

        val langCode = prefs.getString("preferred_lang", AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
        val lang = AppLanguage.entries.find { it.code == langCode } ?: AppLanguage.ENGLISH
        val spokenOutput = prefs.getBoolean("spoken_output_enabled", true)
        val speechRate = prefs.getFloat("speech_rate", 1.0f)
        val voiceToneName = prefs.getString("voice_tone", TtsVoiceTone.NATURAL.name) ?: TtsVoiceTone.NATURAL.name
        val voiceTone = try {
            TtsVoiceTone.valueOf(voiceToneName)
        } catch (_: Exception) {
            TtsVoiceTone.NATURAL
        }
        val haptic = prefs.getBoolean("haptic", true)
        val flashcards = prefs.getBoolean("flashcards_hc", true)

        // Passport facility requirements
        val stepFree = prefs.getBoolean("req_step_free", false)
        val ramp = prefs.getBoolean("req_ramp", false)
        val lift = prefs.getBoolean("req_lift", false)
        val toilet = prefs.getBoolean("req_toilet", false)
        val wheelchairRoute = prefs.getBoolean("req_wheelchair_route", false)
        val tactile = prefs.getBoolean("req_tactile", false)
        val braille = prefs.getBoolean("req_braille", false)
        val audioAssist = prefs.getBoolean("req_audio_assist", false)
        val visualAnnounce = prefs.getBoolean("req_visual_announce", false)
        val parking = prefs.getBoolean("req_parking", false)

        return AccessibilityPreferences(
            highContrast = highContrast,
            fontScale = fontScale,
            dyslexiaSpacing = dyslexiaSpacing,
            reducedMotion = reducedMotion,
            keyboardFriendlyNavigation = keyboardFriendly,
            reduceUnnecessarySteps = reduceSteps,
            preferredLanguage = lang,
            spokenOutputEnabled = spokenOutput,
            speechRate = speechRate,
            voiceTone = voiceTone,
            hapticFeedback = haptic,
            highContrastFlashcards = flashcards,
            requireStepFree = stepFree,
            requireRamp = ramp,
            requireLift = lift,
            requireAccessibleToilet = toilet,
            requireWheelchairRoute = wheelchairRoute,
            requireTactileGuidance = tactile,
            requireBrailleSignage = braille,
            requireAudioAssistance = audioAssist,
            requireVisualAnnouncements = visualAnnounce,
            requireAccessibleParking = parking
        )
    }

    fun updateHighContrast(enabled: Boolean) {
        prefs.edit().putBoolean("high_contrast", enabled).apply()
        _preferences.value = _preferences.value.copy(highContrast = enabled)
    }

    fun updateFontScale(scale: Float) {
        val bounded = scale.coerceIn(1.0f, 1.6f)
        prefs.edit().putFloat("font_scale", bounded).apply()
        _preferences.value = _preferences.value.copy(fontScale = bounded)
    }

    fun updateDyslexiaSpacing(enabled: Boolean) {
        prefs.edit().putBoolean("dyslexia_spacing", enabled).apply()
        _preferences.value = _preferences.value.copy(dyslexiaSpacing = enabled)
    }

    fun updateReducedMotion(enabled: Boolean) {
        prefs.edit().putBoolean("reduced_motion", enabled).apply()
        _preferences.value = _preferences.value.copy(reducedMotion = enabled)
    }

    fun updateKeyboardFriendly(enabled: Boolean) {
        prefs.edit().putBoolean("keyboard_friendly", enabled).apply()
        _preferences.value = _preferences.value.copy(keyboardFriendlyNavigation = enabled)
    }

    fun updateReduceSteps(enabled: Boolean) {
        prefs.edit().putBoolean("reduce_steps", enabled).apply()
        _preferences.value = _preferences.value.copy(reduceUnnecessarySteps = enabled)
    }

    fun updatePreferredLanguage(language: AppLanguage) {
        prefs.edit().putString("preferred_lang", language.code).apply()
        _preferences.value = _preferences.value.copy(preferredLanguage = language)
    }

    fun updateSpokenOutputEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("spoken_output_enabled", enabled).apply()
        _preferences.value = _preferences.value.copy(spokenOutputEnabled = enabled)
    }

    fun updateSpeechRate(rate: Float) {
        val bounded = rate.coerceIn(0.5f, 2.0f)
        prefs.edit().putFloat("speech_rate", bounded).apply()
        _preferences.value = _preferences.value.copy(speechRate = bounded)
    }

    fun updateVoiceTone(tone: TtsVoiceTone) {
        prefs.edit().putString("voice_tone", tone.name).apply()
        _preferences.value = _preferences.value.copy(voiceTone = tone)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("haptic", enabled).apply()
        _preferences.value = _preferences.value.copy(hapticFeedback = enabled)
    }

    // Access Passport facility toggles
    fun toggleFacilityRequirement(facilityKey: String) {
        val current = _preferences.value
        val updated = when (facilityKey) {
            "step_free" -> current.copy(requireStepFree = !current.requireStepFree)
            "ramp" -> current.copy(requireRamp = !current.requireRamp)
            "lift" -> current.copy(requireLift = !current.requireLift)
            "toilet" -> current.copy(requireAccessibleToilet = !current.requireAccessibleToilet)
            "wheelchair_route" -> current.copy(requireWheelchairRoute = !current.requireWheelchairRoute)
            "tactile" -> current.copy(requireTactileGuidance = !current.requireTactileGuidance)
            "braille" -> current.copy(requireBrailleSignage = !current.requireBrailleSignage)
            "audio_assist" -> current.copy(requireAudioAssistance = !current.requireAudioAssistance)
            "visual_announce" -> current.copy(requireVisualAnnouncements = !current.requireVisualAnnouncements)
            "parking" -> current.copy(requireAccessibleParking = !current.requireAccessibleParking)
            else -> current
        }
        persistPassport(updated)
        _preferences.value = updated
    }

    private fun persistPassport(p: AccessibilityPreferences) {
        prefs.edit()
            .putBoolean("req_step_free", p.requireStepFree)
            .putBoolean("req_ramp", p.requireRamp)
            .putBoolean("req_lift", p.requireLift)
            .putBoolean("req_toilet", p.requireAccessibleToilet)
            .putBoolean("req_wheelchair_route", p.requireWheelchairRoute)
            .putBoolean("req_tactile", p.requireTactileGuidance)
            .putBoolean("req_braille", p.requireBrailleSignage)
            .putBoolean("req_audio_assist", p.requireAudioAssistance)
            .putBoolean("req_visual_announce", p.requireVisualAnnouncements)
            .putBoolean("req_parking", p.requireAccessibleParking)
            .apply()
    }

    fun saveAll(preferences: AccessibilityPreferences) {
        prefs.edit()
            .putBoolean("high_contrast", preferences.highContrast)
            .putFloat("font_scale", preferences.fontScale)
            .putBoolean("dyslexia_spacing", preferences.dyslexiaSpacing)
            .putBoolean("reduced_motion", preferences.reducedMotion)
            .putBoolean("keyboard_friendly", preferences.keyboardFriendlyNavigation)
            .putBoolean("reduce_steps", preferences.reduceUnnecessarySteps)
            .putString("preferred_lang", preferences.preferredLanguage.code)
            .putBoolean("spoken_output_enabled", preferences.spokenOutputEnabled)
            .putFloat("speech_rate", preferences.speechRate)
            .putString("voice_tone", preferences.voiceTone.name)
            .putBoolean("haptic", preferences.hapticFeedback)
            .putBoolean("flashcards_hc", preferences.highContrastFlashcards)
            .putBoolean("req_step_free", preferences.requireStepFree)
            .putBoolean("req_ramp", preferences.requireRamp)
            .putBoolean("req_lift", preferences.requireLift)
            .putBoolean("req_toilet", preferences.requireAccessibleToilet)
            .putBoolean("req_wheelchair_route", preferences.requireWheelchairRoute)
            .putBoolean("req_tactile", preferences.requireTactileGuidance)
            .putBoolean("req_braille", preferences.requireBrailleSignage)
            .putBoolean("req_audio_assist", preferences.requireAudioAssistance)
            .putBoolean("req_visual_announce", preferences.requireVisualAnnouncements)
            .putBoolean("req_parking", preferences.requireAccessibleParking)
            .apply()
        _preferences.value = preferences
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _preferences.value = AccessibilityPreferences()
    }
}
