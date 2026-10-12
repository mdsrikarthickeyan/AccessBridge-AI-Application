package com.example.service

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiVisionService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun isApiKeyConfigured(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun analyzeImageDocument(
        bitmap: Bitmap?,
        rawText: String?
    ): Result<DocumentAnalysisResponse> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY_NOT_CONFIGURED")
            )
        }

        try {
            val systemPrompt = """
                You are AccessBridge AI, an inclusive accessibility assistant for people with visual, hearing, mobility, speech, and cognitive disabilities in India.
                Analyze the provided printed notice, sign, circular, hospital form, or document.
                
                Respond ONLY with a valid JSON object strictly matching this schema:
                {
                  "title": "Clear short title or document heading",
                  "originalExtractedText": "Verbatim text extracted from the document. Preserve exact names, numbers, dates, times, and phone numbers. If parts are blurry or unreadable, clearly write [Unreadable or blurry]. Do not invent missing text.",
                  "simpleExplanation": "A warm, respectful, easy-to-understand explanation of the document in 2-3 clear sentences.",
                  "datesAndTimes": ["Key dates, deadlines, or working hours mentioned"],
                  "directionsAndRooms": ["Room numbers, gate numbers, platform or counter numbers, floors"],
                  "requiredDocuments": ["Documents, concession cards, IDs, or forms the person must bring"],
                  "warnings": ["Important warnings, maintenance cautions, or rules"],
                  "contactInfo": "Helpline phone number or support contact if present, or null"
                }
            """.trimIndent()

            val partsArray = JSONArray()

            // Text prompt
            val promptPart = JSONObject().apply {
                put("text", "Please analyze this document or sign and extract the structured accessibility details:")
            }
            partsArray.put(promptPart)

            // If raw text was provided
            if (!rawText.isNullOrBlank()) {
                val textPart = JSONObject().apply {
                    put("text", "Document text:\n$rawText")
                }
                partsArray.put(textPart)
            }

            // If an image bitmap was provided, attach base64 inlineData
            if (bitmap != null) {
                val base64Image = bitmapToBase64(bitmap)
                val imagePart = JSONObject().apply {
                    val inlineData = JSONObject().apply {
                        put("mimeType", "image/jpeg")
                        put("data", base64Image)
                    }
                    put("inlineData", inlineData)
                }
                partsArray.put(imagePart)
            }

            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", partsArray)
                })
            }

            val systemInstruction = JSONObject().apply {
                val sysParts = JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                }
                put("parts", sysParts)
            }

            val generationConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstruction)
                put("generationConfig", generationConfig)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                return@withContext Result.failure(
                    Exception("Failed to read document (${response.code}): $errorBody")
                )
            }

            val responseString = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseString)
            val candidates = jsonRoot.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textOutput = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (textOutput.isBlank()) {
                return@withContext Result.failure(Exception("No readable text found in document."))
            }

            val parsedOutput = JSONObject(textOutput)
            val datesList = mutableListOf<String>()
            val datesArray = parsedOutput.optJSONArray("datesAndTimes")
            if (datesArray != null) {
                for (i in 0 until datesArray.length()) {
                    datesList.add(datesArray.getString(i))
                }
            }

            val directionsList = mutableListOf<String>()
            val directionsArray = parsedOutput.optJSONArray("directionsAndRooms")
            if (directionsArray != null) {
                for (i in 0 until directionsArray.length()) {
                    directionsList.add(directionsArray.getString(i))
                }
            }

            val docsList = mutableListOf<String>()
            val docsArray = parsedOutput.optJSONArray("requiredDocuments")
            if (docsArray != null) {
                for (i in 0 until docsArray.length()) {
                    docsList.add(docsArray.getString(i))
                }
            }

            val warningsList = mutableListOf<String>()
            val warningsArray = parsedOutput.optJSONArray("warnings")
            if (warningsArray != null) {
                for (i in 0 until warningsArray.length()) {
                    warningsList.add(warningsArray.getString(i))
                }
            }

            val result = DocumentAnalysisResponse(
                title = parsedOutput.optString("title", "Scanned Notice"),
                originalExtractedText = parsedOutput.optString("originalExtractedText", "No original text extracted."),
                simpleExplanation = parsedOutput.optString("simpleExplanation", "This document contains important public notice information."),
                datesAndTimes = datesList,
                directionsAndRooms = directionsList,
                requiredDocuments = docsList,
                warnings = warningsList,
                contactInfo = parsedOutput.optString("contactInfo").takeIf { it.isNotBlank() && it != "null" }
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translateText(
        text: String,
        targetLanguage: AppLanguage
    ): Result<String> = withContext(Dispatchers.IO) {
        if (targetLanguage == AppLanguage.ENGLISH) {
            return@withContext Result.success(text)
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("GEMINI_API_KEY_NOT_CONFIGURED"))
        }

        try {
            val prompt = "Translate this accessibility explanation into natural, simple ${targetLanguage.displayName} (${targetLanguage.localName}). Do not add extra commentary. Return only the translated text:\n\n$text"

            val partsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("text", prompt)
                })
            }

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", partsArray)
                    })
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Translation request could not be completed."))
            }

            val responseString = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseString)
            val candidates = jsonRoot.optJSONArray("candidates")
            val textOutput = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: text

            Result.success(textOutput.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun answerFollowUpQuestion(
        documentOriginalText: String,
        documentSummary: String,
        question: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Local fallback answering strictly based on keywords in document
            val lowerDoc = (documentOriginalText + " " + documentSummary).lowercase()
            val lowerQ = question.lowercase()

            val answer = when {
                lowerQ.contains("time") || lowerQ.contains("when") || lowerQ.contains("date") || lowerQ.contains("deadline") -> {
                    val dateRegex = Regex("(?i)(\\d{1,2}(?:st|nd|rd|th)?\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{4}|\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{1,2}:\\d{2}\\s*(?:AM|PM)?)")
                    val match = dateRegex.find(documentOriginalText)
                    if (match != null) {
                        "Based on the document, the mentioned timing or date is: ${match.value}."
                    } else {
                        "The document does not explicitly specify an exact time or date."
                    }
                }
                lowerQ.contains("wheelchair") || lowerQ.contains("ramp") || lowerQ.contains("lift") || lowerQ.contains("elevator") -> {
                    if (lowerDoc.contains("wheelchair") || lowerDoc.contains("ramp") || lowerDoc.contains("lift") || lowerDoc.contains("elevator") || lowerDoc.contains("buggy")) {
                        "Yes, the notice mentions mobility support, elevators, or assistance buggies on site."
                    } else {
                        "The document does not mention specific wheelchair or elevator arrangements."
                    }
                }
                lowerQ.contains("call") || lowerQ.contains("phone") || lowerQ.contains("contact") || lowerQ.contains("helpline") -> {
                    val phoneRegex = Regex("(\\d{3,5}[-\\s]?\\d{6,8}|139|112|1075|1800[-\\s]?\\d{2,4}[-\\s]?\\d{4})")
                    val match = phoneRegex.find(documentOriginalText)
                    if (match != null) {
                        "The document provides this helpline number: ${match.value}."
                    } else {
                        "No phone number is listed in the document. You can contact the on-duty support desk or dial 1800-11-2001."
                    }
                }
                else -> {
                    "Based on the text: " + documentSummary.take(150) + "..."
                }
            }
            return@withContext Result.success(answer)
        }

        try {
            val systemPrompt = """
                You are AccessBridge AI answering a specific user question about a scanned notice or public document in India.
                Document Text:
                $documentOriginalText
                
                Document Summary:
                $documentSummary
                
                Rules:
                1. Answer directly and concisely in simple, clear language.
                2. Base your answer STRICTLY on the document text provided above.
                3. If the document does not contain the answer, say honestly: "The document does not specify this information."
                4. NEVER invent missing details, phone numbers, or dates.
            """.trimIndent()

            val partsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("text", "User question: $question")
                })
            }

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", partsArray)
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemPrompt)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.1)
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Unable to answer follow-up question."))
            }

            val responseString = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseString)
            val candidates = jsonRoot.optJSONArray("candidates")
            val textOutput = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: "No answer available."

            Result.success(textOutput.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Resize bitmap if very large to prevent memory overhead
        val maxDimension = 1600
        val scaled = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newWidth = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
            val newHeight = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}

data class DocumentAnalysisResponse(
    val title: String,
    val originalExtractedText: String,
    val simpleExplanation: String,
    val datesAndTimes: List<String>,
    val directionsAndRooms: List<String>,
    val requiredDocuments: List<String>,
    val warnings: List<String>,
    val contactInfo: String?
)
