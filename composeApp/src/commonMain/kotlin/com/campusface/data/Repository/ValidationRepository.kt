package com.campusface.data.Repository

import com.campusface.data.BASE_URL
// Certifique-se de que a classe OrganizationMember está acessível aqui
// import com.campusface.data.Repository.OrganizationMember

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay // Usado para a simulação
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- MODELS JÁ EXISTENTES ---
@Serializable
data class ValidateCodeRequest(
    val code: String
)

@Serializable
data class ValidationResponseData(
    val valid: Boolean,
    val message: String,
    val member: OrganizationMember? = null
)

@Serializable
data class ValidationApiResponse(
    val success: Boolean,
    val message: String,
    val data: ValidationResponseData? = null
)

@Serializable
data class GenerateCodeRequest(
    val organizationId: String
)

@Serializable
data class GeneratedCodeData(
    val code: String,
    val expirationTime: String
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

    // --- (1) CREATE: GERAR QR CODE ---
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

    // --- (2) READ: VALIDAR QR CODE ---
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

                val rawResponse = httpResponse.body<ValidationApiResponse>()

                if (rawResponse.success && rawResponse.data != null) {
                    onSuccess(rawResponse.data)
                } else {
                    onError(rawResponse.message)
                }

            } catch (e: Exception) {
                onError("Erro de validação: ${e.message}")
            }
        }
    }

    // --- (3) UPDATE: ESTENDER VALIDADE (SIMULAÇÃO) ---
    /**
     * Simula uma atualização no recurso QR Code.
     * Como o backend não possui PUT /validate/qr-code/{code},
     * simulamos uma chamada de rede e retornamos o mesmo código com data futura.
     */
    fun extendQrCodeValidity(
        currentCode: String,
        token: String,
        onSuccess: (GeneratedCodeData) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("SIMULAÇÃO: Enviando PUT para atualizar QR Code $currentCode...")

                // 1. Simula delay de rede (1.5 segundos)
                delay(1500)

                // 2. Simula uma resposta de sucesso do servidor
                // Vamos fingir que o servidor estendeu a validade por 1 hora

                // NOTA: Se fosse real, seria assim:
                /*
                val response = client.put(BASE_URL + "/validate/qr-code/$currentCode") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                */

                // 3. Retorna o objeto atualizado
                // Aqui geramos uma data fake no futuro para a simulação
                // (Para funcionar perfeitamente, o app teria que ignorar a validação real do backend neste momento)
                val simulatedNewExpiration = "2099-12-31T23:59:59.999Z"

                val updatedData = GeneratedCodeData(
                    code = currentCode, // Mantém o mesmo número
                    expirationTime = simulatedNewExpiration
                )

                println("SIMULAÇÃO: Sucesso! Validade estendida.")
                onSuccess(updatedData)

            } catch (e: Exception) {
                onError("Erro na simulação: ${e.message}")
            }
        }
    }

    // --- (4) DELETE: INVALIDAR/CANCELAR QR CODE ---
    fun invalidateQrCode(
        code: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.post(BASE_URL + "/validate/qr-code/invalidate") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(ValidateCodeRequest(code))
                }

                if (httpResponse.status.value >= 400) {
                    onError("Erro ${httpResponse.status.value}")
                    return@launch
                }

                val response = httpResponse.body<ValidationApiResponse>()

                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                onError("Erro ao invalidar: ${e.message}")
            }
        }
    }
}