package com.cd.uielementmanager.presentation.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale.US)
                    isInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                            result != TextToSpeech.LANG_NOT_SUPPORTED
                    if (isInitialized) {
                        tts.setSpeechRate(1.0f)
                        tts.setPitch(1.0f)
                    }
                }
            } else {
                isInitialized = false
                Log.e("TextToSpeechManager", "Initialization failed")
            }
        }
    }


    fun speak(text: String) {
        if (!isInitialized) {
            return
        }

        stop() // Stop any ongoing speech
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "instruction")
    }

    fun stop() {
        if (!isInitialized) {
            textToSpeech?.stop()
        }
    }

    fun shutdown() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}