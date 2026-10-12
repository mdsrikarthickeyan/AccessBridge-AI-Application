package com.example.service

import com.example.model.AppLanguage

sealed class TranslationResult {
    data class Success(
        val originalText: String,
        val translatedText: String,
        val sourceLanguage: AppLanguage,
        val targetLanguage: AppLanguage,
        val providerNote: String
    ) : TranslationResult()

    data class Error(val message: String) : TranslationResult()
}

class CommunicationTranslationService {

    // Curated high-accuracy bilingual translations for common transit and accessibility vocabulary
    private val commonPhrases = mapOf(
        "Please speak slowly." to mapOf(
            AppLanguage.HINDI to "कृपया धीरे बोलें।",
            AppLanguage.TAMIL to "தயவுசெய்து மெதுவாக பேசுங்கள்.",
            AppLanguage.TELUGU to "దయచేసి నెమ్మదిగా మాట్లాడండి.",
            AppLanguage.KANNADA to "ದಯವಿಟ್ಟು ನಿಧಾನವಾಗಿ ಮಾತನಾಡಿ.",
            AppLanguage.BENGALI to "দয়া করে আস্তে কথা বলুন।",
            AppLanguage.MARATHI to "कृपया हळू बोला."
        ),
        "Please write that down." to mapOf(
            AppLanguage.HINDI to "कृपया इसे लिखकर दें।",
            AppLanguage.TAMIL to "தயவுசெய்து அதை எழுதிக் காட்டுங்கள்.",
            AppLanguage.TELUGU to "దయచేసి దాన్ని రాసి చూపించండి.",
            AppLanguage.KANNADA to "ದಯವಿಟ್ಟು ಅದನ್ನು ಬರೆದು ತೋರಿಸಿ.",
            AppLanguage.BENGALI to "দয়া করে লিখে দিন।",
            AppLanguage.MARATHI to "कृपया ते लिहून द्या."
        ),
        "I need step-free access." to mapOf(
            AppLanguage.HINDI to "मुझे बिना सीढ़ियों (रैंप या लिफ्ट) वाला रास्ता चाहिए।",
            AppLanguage.TAMIL to "எனக்கு படிகள் இல்லாத பாதை (சாய்வுதளம் அல்லது லிப்ட்) தேவை.",
            AppLanguage.TELUGU to "నాకు మెట్లు లేని దారి (ర్యాంప్ లేదా లిఫ్ట్) కావాలి.",
            AppLanguage.KANNADA to "ನನಗೆ ಮೆಟ್ಟಿಲುಗಳಿಲ್ಲದ ಮಾರ್ಗ ಬೇಕು.",
            AppLanguage.BENGALI to "আমার সিঁড়িহীন প্রবেশপথ দরকার।",
            AppLanguage.MARATHI to "मला पायऱ्या नसणारा मार्ग हवा आहे."
        ),
        "Could you show me where to go?" to mapOf(
            AppLanguage.HINDI to "क्या आप मुझे रास्ता दिखा सकते हैं?",
            AppLanguage.TAMIL to "எங்கே செல்ல வேண்டும் என்று எனக்கு காட்ட முடியுமா?",
            AppLanguage.TELUGU to "నేను ఎక్కడికి వెళ్ళాలో చూపించగలరా?",
            AppLanguage.KANNADA to "ನಾನು ಎಲ್ಲಿಗೆ ಹೋಗಬೇಕೆಂದು ತೋರಿಸಬಹುದೇ?",
            AppLanguage.BENGALI to "আমাকে কি রাস্তা দেখিয়ে দিতে পারেন?",
            AppLanguage.MARATHI to "मला कुठे जायचे ते दाखवू शकता का?"
        ),
        "Please give me a moment." to mapOf(
            AppLanguage.HINDI to "कृपया मुझे एक पल का समय दें।",
            AppLanguage.TAMIL to "தயவுசெய்து எனக்கு ஒரு நிமிடம் அவகாசம் கொடுங்கள்.",
            AppLanguage.TELUGU to "దయచేసి నాకు కాస్త సమయం ఇవ్వండి.",
            AppLanguage.KANNADA to "ದಯವಿಟ್ಟು ನನಗೆ ಸ್ವಲ್ಪ ಸಮಯ ಕೊಡಿ.",
            AppLanguage.BENGALI to "দয়া করে আমাকে একটু সময় দিন।",
            AppLanguage.MARATHI to "कृपया मला एक क्षण वेळ द्या."
        ),
        "I need assistance." to mapOf(
            AppLanguage.HINDI to "मुझे सहायता चाहिए।",
            AppLanguage.TAMIL to "எனக்கு உதவி தேவை.",
            AppLanguage.TELUGU to "నాకు సహాయం కావాలి.",
            AppLanguage.KANNADA to "ನನಗೆ ಸಹಾಯ ಬೇಕು.",
            AppLanguage.BENGALI to "আমার সাহায্য প্রয়োজন।",
            AppLanguage.MARATHI to "मला मदतीची गरज आहे."
        ),
        "Where is the accessible restroom?" to mapOf(
            AppLanguage.HINDI to "दिव्यांगजन सुलभ शौचालय कहाँ है?",
            AppLanguage.TAMIL to "சக்கர நாற்காலி கழிப்பறை எங்கே?",
            AppLanguage.TELUGU to "వీల్ చైర్ వాష్ రూమ్ ఎక్కడ ఉంది?",
            AppLanguage.KANNADA to "ಅಂಗವಿಕಲರ ಶೌಚಾಲಯ ಎಲ್ಲಿದೆ?",
            AppLanguage.BENGALI to "প্রতিবন্ধী বান্ধব শৌচাগার কোথায়?",
            AppLanguage.MARATHI to "सुलभ शौचालय कुठे आहे?"
        ),
        "I need drinking water, please." to mapOf(
            AppLanguage.HINDI to "मुझे पीने का पानी चाहिए, कृपया।",
            AppLanguage.TAMIL to "எனக்கு குடிக்க தண்ணீர் வேண்டும், தயவுசெய்து.",
            AppLanguage.TELUGU to "నాకు తాగడానికి నీరు కావాలి, దయచేసి.",
            AppLanguage.KANNADA to "ನನಗೆ ಕುಡಿಯಲು ನೀರು ಬೇಕು, ದಯವಿಟ್ಟು.",
            AppLanguage.BENGALI to "আমাকে খাওয়ার জল দিন, দয়া করে।",
            AppLanguage.MARATHI to "मला पिण्याचे पाणी हवे आहे, कृपया."
        )
    )

    fun translate(
        text: String,
        sourceLanguage: AppLanguage,
        targetLanguage: AppLanguage
    ): TranslationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return TranslationResult.Error("Please enter a message to translate.")
        }

        if (sourceLanguage == targetLanguage) {
            return TranslationResult.Success(
                originalText = trimmed,
                translatedText = trimmed,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                providerNote = "Source and target languages are the same."
            )
        }

        // Check if there is an exact match in our verified dictionary
        for ((phraseKey, transMap) in commonPhrases) {
            if (trimmed.equals(phraseKey, ignoreCase = true)) {
                val direct = transMap[targetLanguage]
                if (direct != null) {
                    return TranslationResult.Success(
                        originalText = trimmed,
                        translatedText = direct,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        providerNote = "Verified offline accessibility dictionary translation. You can edit this text."
                    )
                }
            }
        }

        // Check if the input is a known translated phrase from Hindi/Tamil etc. back to English
        for ((phraseKey, transMap) in commonPhrases) {
            for ((lang, transText) in transMap) {
                if (trimmed == transText && targetLanguage == AppLanguage.ENGLISH) {
                    return TranslationResult.Success(
                        originalText = trimmed,
                        translatedText = phraseKey,
                        sourceLanguage = lang,
                        targetLanguage = AppLanguage.ENGLISH,
                        providerNote = "Verified offline dictionary translation."
                    )
                }
            }
        }

        // Contextual rule-based fallback translation for everyday phrases
        val simpleTranslated = buildContextualTranslation(trimmed, sourceLanguage, targetLanguage)

        return TranslationResult.Success(
            originalText = trimmed,
            translatedText = simpleTranslated,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            providerNote = "Draft translation. Translations are not guaranteed accurate — please review and edit before sharing."
        )
    }

    private fun buildContextualTranslation(
        text: String,
        source: AppLanguage,
        target: AppLanguage
    ): String {
        val lower = text.lowercase()
        return when (target) {
            AppLanguage.HINDI -> {
                when {
                    lower.contains("water") -> "मुझे पीने का पानी चाहिए।"
                    lower.contains("help") || lower.contains("assist") -> "कृपया मेरी सहायता करें।"
                    lower.contains("ticket") -> "मुझे एक टिकट चाहिए।"
                    lower.contains("doctor") || lower.contains("hospital") -> "मुझे डॉक्टर या अस्पताल जाना है।"
                    lower.contains("toilet") || lower.contains("washroom") || lower.contains("restroom") -> "शौचालय कहाँ है?"
                    lower.contains("bus") -> "बस कहाँ मिलेगी?"
                    lower.contains("train") || lower.contains("metro") -> "मेट्रो या ट्रेन कहाँ है?"
                    lower.contains("slow") -> "कृपया धीरे बोलें।"
                    lower.contains("write") -> "कृपया इसे लिख दें।"
                    lower.contains("ramp") || lower.contains("lift") || lower.contains("step-free") -> "यहाँ रैंप या लिफ्ट कहाँ है?"
                    else -> "[$text - हिन्दी अनुवाद]"
                }
            }
            AppLanguage.TAMIL -> {
                when {
                    lower.contains("water") -> "எனக்கு தண்ணீர் வேண்டும்."
                    lower.contains("help") -> "தயவுசெய்து எனக்கு உதவுங்கள்."
                    lower.contains("ticket") -> "எனக்கு ஒரு டிக்கெட் வேண்டும்."
                    lower.contains("toilet") || lower.contains("restroom") -> "கழிப்பறை எங்கே உள்ளது?"
                    lower.contains("bus") -> "பேருந்து எங்கே வரும்?"
                    lower.contains("train") -> "ரயில் எங்கே வரும்?"
                    lower.contains("slow") -> "தயவுசெய்து மெதுவாக பேசுங்கள்."
                    lower.contains("write") -> "தயவுசெய்து எழுதி காட்டுங்கள்."
                    else -> "[$text - தமிழ் மொழிபெயர்ப்பு]"
                }
            }
            AppLanguage.TELUGU -> {
                when {
                    lower.contains("water") -> "నాకు నీరు కావాలి."
                    lower.contains("help") -> "దయచేసి నాకు సహాయం చేయండి."
                    lower.contains("ticket") -> "నాకు ఒక టికెట్ కావాలి."
                    lower.contains("toilet") || lower.contains("restroom") -> "వాష్ రూమ్ ఎక్కడ ఉంది?"
                    lower.contains("bus") -> "బస్సు ఎక్కడ వస్తుంది?"
                    lower.contains("slow") -> "దయచేసి నెమ్మదిగా మాట్లాడండి."
                    lower.contains("write") -> "దయచేసి రాసి చూపించండి."
                    else -> "[$text - తెలుగు అనువాదం]"
                }
            }
            AppLanguage.KANNADA -> {
                when {
                    lower.contains("water") -> "ನನಗೆ ನೀರು ಬೇಕು."
                    lower.contains("help") -> "ದಯವಿಟ್ಟು ನನಗೆ ಸಹಾಯ ಮಾಡಿ."
                    lower.contains("ticket") -> "ನನಗೆ ಒಂದು ಟಿಕೆಟ್ ಬೇಕು."
                    lower.contains("toilet") || lower.contains("restroom") -> "ಶೌಚಾಲಯ ಎಲ್ಲಿದೆ?"
                    lower.contains("bus") -> "ಬಸ್ ಎಲ್ಲಿ ಬರುತ್ತದೆ?"
                    lower.contains("slow") -> "ದಯವಿಟ್ಟು ನಿಧಾನವಾಗಿ ಮಾತನಾಡಿ."
                    lower.contains("write") -> "ದಯವಿಟ್ಟು ಬರೆದು ತೋರಿಸಿ."
                    else -> "[$text - ಕನ್ನಡ ಅನುವಾದ]"
                }
            }
            AppLanguage.BENGALI -> {
                when {
                    lower.contains("water") -> "আমার জল দরকার।"
                    lower.contains("help") -> "দয়া করে সাহায্য করুন।"
                    lower.contains("ticket") -> "আমার একটি টিকিট দরকার।"
                    lower.contains("toilet") -> "শৌচাগার কোথায়?"
                    else -> "[$text - বাংলা অনুবাদ]"
                }
            }
            AppLanguage.MARATHI -> {
                when {
                    lower.contains("water") -> "मला पाणी हवे आहे."
                    lower.contains("help") -> "कृपया मला मदत करा."
                    lower.contains("ticket") -> "मला एक तिकीट हवे आहे."
                    lower.contains("toilet") -> "शौचालय कुठे आहे?"
                    else -> "[$text - मराठी भाषांतर]"
                }
            }
            AppLanguage.ENGLISH -> {
                "[$text in English]"
            }
        }
    }
}
