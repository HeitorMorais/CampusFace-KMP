package com.campusface.data.Repository

import com.campusface.data.BASE_URL
import com.campusface.data.Model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Ktor imports
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

@Serializable
data class EntryRequestData(
    val hubCode : String,
    val role : String
)

@Serializable
data class EntryRequest(
    val id: String,
    val hubCode: String,
    val role: String,
    val status: String,
    val requestedAt: String,
    val user: User
)

// Resposta para CRIAR (retorna 1 objeto)
@Serializable
data class EntryRequestResponse(
    val message: String,
    val success: Boolean,
    val data: EntryRequest? = null
)

// NOVO: Resposta para LISTAR (retorna uma lista de objetos)
@Serializable
data class EntryRequestListResponse(
    val message: String,
    val success: Boolean,
    val data: List<EntryRequest>? = emptyList()
)

@Serializable
data class EntryRequestCreateBody(
    val hubCode: String,
    val role: String?
)

class EntryRequestRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // Função existente: Criar Requisição
    fun entryRequestCreate(
        hubCode: String,
        role: String?,
        token: String?,
        onSuccess: (EntryRequest) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("HubCode: $hubCode")
                println("Role: $role")
                println("Token: $token")

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

    // NOVO: Função para Listar Requisições do Usuário
    fun listMyRequests(
        token: String?,
        onSuccess: (List<EntryRequest>) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Chama o endpoint GET /my-requests que criamos anteriormente
                val httpResponse = client.get("$BASE_URL/entry-requests/my-requests") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                // Tratamento de erro HTTP
                if (httpResponse.status.value >= 400) {
                    val raw = httpResponse.bodyAsText()
                    onError("Erro ao buscar lista (${httpResponse.status.value}): $raw")
                    return@launch
                }

                // Desserializa usando a nova classe de Lista
                val response = httpResponse.body<EntryRequestListResponse>()

                if (response.success && response.data != null) {
                    onSuccess(response.data)
                } else {
                    // Se success for false ou data for null, retorna lista vazia ou erro
                    onError(response.message)
                }

            } catch (e: Exception) {
                println("ListMyRequests ERROR: ${e.message}")
                e.printStackTrace()
                onError("Erro de conexão: ${e.message}")
            }
        }
    }
}