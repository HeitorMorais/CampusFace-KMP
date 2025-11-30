package com.campusface.data.Repository

import com.campusface.data.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- Models baseados no Swagger ---
@Serializable
data class GenerateCodeRequest(
    val organizationId: String
)

@Serializable
data class GeneratedCodeData(
    val code: String,
    val expirationTime: String // Formato ISO 8601 ex: 2025-11-29T06:28:37.736Z
)

@Serializable
data class GenerateCodeResponse(
    val success: Boolean,
    val message: String,
    val data: GeneratedCodeData? = null
)

// --- Repository ---
class ValidationRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    fun generateQrCode(
        organizationId: String,
        token: String,
        onSuccess: (GeneratedCodeData) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.post(BASE_URL + "/validate/qr-code/generate") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(GenerateCodeRequest(organizationId))
                }

                if (httpResponse.status.value >= 400) {
                    onError("Erro ${httpResponse.status.value}")
                    return@launch
                }

                val response = httpResponse.body<GenerateCodeResponse>()

                if (response.success && response.data != null) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onError("Erro de conexão: ${e.message}")
            }
        }
    }
}