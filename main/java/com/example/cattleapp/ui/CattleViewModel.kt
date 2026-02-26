package com.example.cattleapp.ui

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattleapp.BuildConfig
import com.example.cattleapp.ai.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CattleViewModel(app: Application) : AndroidViewModel(app) {
    private val _answer = MutableStateFlow("")
    val answer = _answer.asStateFlow()

    private val apiKey: String = BuildConfig.GENAI_API_KEY

    private fun ensureApiKeyOrShowMessage(): Boolean {
        if (apiKey.isBlank()) {
            _answer.value = "Missing API key. Add GENAI_API_KEY in local.properties and rebuild."
            return false
        }
        return true
    }

    fun askByBreed(breed: String) {
        if (breed.isBlank()) return
        if (!ensureApiKeyOrShowMessage()) return
        _answer.value = "Thinking..."
        viewModelScope.launch {
            runCatching {
                GeminiClient.askAboutBreed(apiKey, breed.trim())
            }.onSuccess { _answer.value = it }
                .onFailure { _answer.value = "Error: ${it.message}" }
        }
    }

    fun identifyByImage(resolver: ContentResolver, uri: Uri) {
        if (!ensureApiKeyOrShowMessage()) return
        _answer.value = "Analyzing image..."
        viewModelScope.launch {
            runCatching {
                val stream = resolver.openInputStream(uri) ?: error("Cannot open image")
                val bytes = stream.readBytes().also { stream.close() }
                GeminiClient.identifyBreedAndExplain(apiKey, bytes, "image/jpeg")
            }.onSuccess { _answer.value = it }
                .onFailure { _answer.value = "Error: ${it.message}" }
        }
    }
}


