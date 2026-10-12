package com.example.model

enum class CommCategory(val title: String) {
    DAILY_NEEDS("Daily Needs"),
    TRANSIT("Transit & Travel"),
    HEALTH("Health & Medical"),
    EMERGENCY("Help & Emergency"),
    DIRECTIONS("Finding Directions")
}

data class CommunicationCard(
    val id: String,
    val category: CommCategory,
    val englishText: String,
    val hindiText: String,
    val tamilText: String,
    val teluguText: String,
    val kannadaText: String,
    val iconName: String,
    val phoneticGuide: String? = null
) {
    fun getTextForLanguage(language: AppLanguage): String {
        return when (language) {
            AppLanguage.ENGLISH -> englishText
            AppLanguage.HINDI -> hindiText
            AppLanguage.TAMIL -> tamilText
            AppLanguage.TELUGU -> teluguText
            AppLanguage.KANNADA -> kannadaText
            AppLanguage.BENGALI -> hindiText // fallback
            AppLanguage.MARATHI -> hindiText // fallback
        }
    }
}
