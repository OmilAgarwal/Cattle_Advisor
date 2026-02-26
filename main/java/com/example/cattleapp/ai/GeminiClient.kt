package com.example.cattleapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val MODEL = "models/gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1/"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // ✅ Extended timeout to prevent slow-response errors
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    suspend fun askAboutBreed(apiKey: String, breed: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            You are an expert on cattle breeds. For the breed: "$breed"
            Answer concisely with bullet points:
            - Where they are usually found (regions in India and globally)
            - Suitable food/diet
            - Typical uses/value (milk, draft, etc.)
            - Recommended weather and living conditions
            - Safety/care conditions
            - Usual price in India (give a realistic recent range; note that prices vary by age, region, and quality)
        """.trimIndent()

        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
        }

        val request = buildRequest(apiKey, body)
        safeExecute(request)
    }

    // ✅ Image compression helper (prevents large payload timeout)
    private suspend fun compressImage(context: Context, imageBytes: ByteArray): ByteArray =
        withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val scaled = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
            val output = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, output)
            output.toByteArray()
        }

    suspend fun identifyBreedAndExplain(
        apiKey: String,
        imageBytes: ByteArray,
        mimeType: String,
        context: Context? = null
    ): String = withContext(Dispatchers.IO) {
        val instruction = """
            Identify the cattle breed in this image.
            Then provide:
            - Breed name
            - Where they are usually found (India/regions)
            - Suitable food/diet
            - Typical uses/value
            - Recommended weather and living conditions
            - Safety/care conditions
            - Usual price in India (recent range, with uncertainty noted)
            If uncertain, state uncertainty and give best guess with rationale.
        """.trimIndent()

        // ✅ Compress before sending (reduces size drastically)
        val safeBytes = if (context != null) compressImage(context, imageBytes) else imageBytes
        val base64 = Base64.encodeToString(safeBytes, Base64.NO_WRAP)

        Log.d("GeminiClient", "Encoded image size: ${base64.length / 1024} KB")

        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", instruction) })
                        put(JSONObject().apply {
                            put("inline_data", JSONObject().apply {
                                put("mime_type", mimeType)
                                put("data", base64)
                            })
                        })
                    })
                })
            })
        }

        val request = buildRequest(apiKey, body)
        safeExecute(request)
    }

    private fun buildRequest(apiKey: String, body: JSONObject): Request {
        val url = "$BASE_URL$MODEL:generateContent?key=$apiKey"
        val requestBody: RequestBody = body.toString().toRequestBody(jsonMediaType)
        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }

    // ✅ Unified safe API call with detailed fallback
    private fun safeExecute(request: Request): String {
        return try {
            httpClient.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    Log.e("GeminiClient", "HTTP ${resp.code}: ${resp.body?.string()}")
                    return "⚠️ API Error: ${resp.code} (Please try again)"
                }
                val json = JSONObject(resp.body?.string() ?: "{}")
                extractText(json)
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "Error: ${e.message}", e)
            "⚠️ Timeout or network error. Please check your internet and try again."
        }
    }

    private fun extractText(json: JSONObject): String {
        val candidates = json.optJSONArray("candidates") ?: return "No answer."
        if (candidates.length() == 0) return "No answer."
        val content = candidates.optJSONObject(0)?.optJSONObject("content") ?: return "No answer."
        val parts = content.optJSONArray("parts") ?: return "No answer."
        if (parts.length() == 0) return "No answer."
        return parts.optJSONObject(0)?.optString("text").orEmpty().ifBlank { "No answer." }
    }
}

