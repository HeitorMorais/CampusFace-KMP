package com.campusface.data.Repository

import com.campusface.data.BASE_URL
import com.campusface.data.Model.User
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

// ==========================================
// 1. MODELS
// ==========================================

@Serializable
data class EntryRequest(
    val id: String,
    val hubCode: String,
    val role: String,
    val status: String,
    val requestedAt: String? = null,
    val user: User
)

@Serializable
data class EntryRequestCreateBody(
    val hubCode: String,
    val role: String?
)

@Serializable
data class EntryRequestResponse(
    val message: String,
    val success: Boolean,
    val data: EntryRequest? = null
)

@Serializable
data class EntryRequestListResponse(
    val message: String,
    val success: Boolean,
    val data: List<EntryRequest> = emptyList()
)

@Serializable
data class ActionResponse(
    val message: String,
    val success: Boolean
)

// ==========================================
// 2. REPOSITORY
// ==========================================

class EntryRequestRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // --- CRIAR SOLICITAÇÃO ---
    fun entryRequestCreate(
        hubCode: String,
        role: String?,
        token: String?,
        onSuccess: (EntryRequest) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.post(BASE_URL + "/entry-requests/create") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        EntryRequestCreateBody(
                            hubCode = hubCode,
                            role = role?.uppercase()
                        )
                    )
                }

                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $raw")
                    return@launch
                }

                val response = httpResponse.body<EntryRequestResponse>()

                if (response.success && response.data != null) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                println("EntryRequestCreate ERROR: ${e.message}")
                e.printStackTrace()
                onError("Erro inesperado: ${e.message}")
            }
        }
    }

    // --- LISTAR MINHAS SOLICITAÇÕES ---
    fun listMyRequests(
        token: String?,
        onSuccess: (List<EntryRequest>) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.get(BASE_URL + "/entry-requests/my-requests") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ao buscar lista (${httpResponse.status.value}): $raw")
                    return@launch
                }

                val response = httpResponse.body<EntryRequestListResponse>()

                if (response.success) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                println("ListMyRequests ERROR: ${e.message}")
                e.printStackTrace()
                onError("Erro de conexão: ${e.message}")
            }
        }
    }

    // --- LISTAR PENDENTES DO HUB (ADMIN) ---
    fun listPendingRequestsByHub(
        hubCode: String,
        token: String,
        onSuccess: (List<EntryRequest>) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.get(BASE_URL + "/entry-requests/organization/$hubCode") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $raw")
                    return@launch
                }

                val response = httpResponse.body<EntryRequestListResponse>()
                if (response.success) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            }
        }
    }

    // --- APROVAR ---
    fun approveRequest(
        requestId: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        performAction(requestId, "approve", token, onSuccess, onError)
    }

    // --- REJEITAR ---
    fun rejectRequest(
        requestId: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        performAction(requestId, "reject", token, onSuccess, onError)
    }

    // ==========================================
    // NOVAS FUNÇÕES ADICIONADAS
    // ==========================================

    // --- BUSCAR POR ID (READ SINGLE) ---
    fun getEntryRequestById(
        requestId: String,
        token: String,
        onSuccess: (EntryRequest) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.get(BASE_URL + "/entry-requests/$requestId") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $raw")
                    return@launch
                }

                val response = httpResponse.body<EntryRequestResponse>()

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

    // --- DELETAR SOLICITAÇÃO (CANCELAR) ---
    fun deleteEntryRequest(
        requestId: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.delete(BASE_URL + "/entry-requests/$requestId") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $raw")
                    return@launch
                }

                // Assume-se que o backend retorna um ActionResponse ou similar no delete
                val response = httpResponse.body<ActionResponse>()

                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            }
        }
    }

    // ==========================================
    // AUXILIARES
    // ==========================================

    // --- AUXILIAR PRIVADO ---
    private fun performAction(
        requestId: String,
        action: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.post(BASE_URL + "/entry-requests/$requestId/$action") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                if (httpResponse.status.value >= 400) {
                    onError("Erro ${httpResponse.status.value}")
                    return@launch
                }

                val response = httpResponse.body<ActionResponse>()
                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError("Erro: ${e.message}")
            }
        }
    }
}