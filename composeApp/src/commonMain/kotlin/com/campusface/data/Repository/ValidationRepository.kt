package com.campusface.data.Repository

import com.campusface.data.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
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

// --- MODELS (Baseados no Swagger) ---
@Serializable
data class ValidateCodeRequest(
    val code: String
)

@Serializable
data class ValidationResponseData(
    val valid: Boolean,
    val message: String,
    val member: OrganizationMember? = null // Retorna os dados do membro se válido
)

@Serializable
data class ValidationApiResponse(
    val success: Boolean,
    val message: String,
    val data: ValidationResponseData? = null
)
@Serializable
data class GenerateCodeRequest(
    val organizationId: String // O Swagger pede exatamente este campo no body
)

@Serializable
data class GeneratedCodeData(
    val code: String,
    val expirationTime: String // ISO 8601 ex: 2025-11-30T03:11:44.492Z
)

@Serializable
data class GenerateCodeResponse(
    val success: Boolean,
    val message: String,
    val data: GeneratedCodeData? = null
)

// --- REPOSITORY ---

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
                // POST /validate/qr-code/generate
                val httpResponse = client.post(BASE_URL + "/validate/qr-code/generate") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    // Envia o JSON { "organizationId": "..." }
                    setBody(GenerateCodeRequest(organizationId))
                }

                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $raw")
                    return@launch
                }

                val response = httpResponse.body<GenerateCodeResponse>()

                if (response.success && response.data != null) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            }
        }
    }
    fun validateQrCode(
        code: String,
        token: String,
        onSuccess: (ValidationResponseData) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.post(BASE_URL + "/validate/qr-code") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(ValidateCodeRequest(code))
                }

                // API retorna 200 para sucesso e 422 para inválido/expirado
                val rawResponse = httpResponse.body<ValidationApiResponse>()

                if (rawResponse.success && rawResponse.data != null) {
                    onSuccess(rawResponse.data)
                } else {
                    // Se a API retornar sucesso=false no body, tratamos como erro
                    onError(rawResponse.message)
                }

            } catch (e: Exception) {
                // Erros de rede ou 403 (permissão) caem aqui
                onError("Erro de validação: ${e.message}")
            }
        }
    }
}