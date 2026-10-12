package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class SpeechRecognitionState {
    object Idle : SpeechRecognitionState()
    object Listening : SpeechRecognitionState()
    data class Processing(val partialText: String) : SpeechRecognitionState()
    data class Success(val recognizedText: String) : SpeechRecognitionState()
    data class Error(val message: String) : SpeechRecognitionState()
}

class SpeechToTextManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val _state = MutableStateFlow<SpeechRecognitionState>(SpeechRecognitionState.Idle)
    val state: StateFlow<SpeechRecognitionState> = _state.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        language: AppLanguage = AppLanguage.ENGLISH,
        onResult: ((String) -> Unit)? = null
    ) {
        if (!isRecognitionAvailable()) {
            _state.value = SpeechRecognitionState.Error(
                "Speech recognition is not available on this device. You can type your message manually below."
            )
            return
        }

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _state.value = SpeechRecognitionState.Listening
                    }

                    override fun onBeginningOfSpeech() {
                        _state.value = SpeechRecognitionState.Listening
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _state.value = SpeechRecognitionState.Processing(_partialText.value)
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your microphone."
                            SpeechRecognizer.ERROR_CLIENT -> "Client error. Please try speaking again."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is needed to turn speech into text."
                            SpeechRecognizer.ERROR_NETWORK -> "Network connection issue. You can still type your message manually."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Please try again."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech was detected. Please try speaking a bit louder or closer to the microphone."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service is busy. Please try again in a moment."
                            SpeechRecognizer.ERROR_SERVER -> "Speech server returned an error. Manual typing is always available."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Tap the microphone when you are ready to speak."
                            else -> "Could not capture speech. You can type your message directly."
                        }
                        _state.value = SpeechRecognitionState.Error(errorMessage)
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim() ?: ""
                        if (text.isNotBlank()) {
                            _partialText.value = text
                            _state.value = SpeechRecognitionState.Success(text)
                            onResult?.invoke(text)
                        } else {
                            _state.value = SpeechRecognitionState.Error("No clear speech detected. Please try again.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partial = matches?.firstOrNull()?.trim() ?: ""
                        if (partial.isNotBlank()) {
                            _partialText.value = partial
                            _state.value = SpeechRecognitionState.Processing(partial)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

                val localeTag = when (language) {
                    AppLanguage.ENGLISH -> "en-IN"
                    AppLanguage.HINDI -> "hi-IN"
                    AppLanguage.TAMIL -> "ta-IN"
                    AppLanguage.TELUGU -> "te-IN"
                    AppLanguage.KANNADA -> "kn-IN"
                    AppLanguage.BENGALI -> "bn-IN"
                    AppLanguage.MARATHI -> "mr-IN"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeTag)
            }

            _partialText.value = ""
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            _state.value = SpeechRecognitionState.Error(
                "Could not start speech recognition (${e.localizedMessage ?: "Unknown"}). Manual typing is always available."
            )
        }
    }

    fun stopListening() {
        try {
            _isListening.value = false
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
    }

    fun resetState() {
        _state.value = SpeechRecognitionState.Idle
        _partialText.value = ""
    }

    fun destroy() {
        stopListening()
    }
}
