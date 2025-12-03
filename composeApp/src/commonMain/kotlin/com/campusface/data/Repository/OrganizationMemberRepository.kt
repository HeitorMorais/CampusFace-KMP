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
data class OrganizationMember(
    val id: String,
    val role: String,   // "MEMBER", "ADMIN", "VALIDATOR"
    val status: String, // "PENDING", "ACTIVE", "INACTIVE"
    val joinedAt: String? = null,
    val user: User
)

@Serializable
data class MemberListResponse(
    val success: Boolean,
    val message: String,
    val data: List<OrganizationMember> = emptyList()
)

@Serializable
data class MemberResponse(
    val success: Boolean,
    val message: String,
    val data: OrganizationMember? = null
)

// DTO para enviar na atualização
@Serializable
data class MemberUpdateRequest(
    val role: String? = null,
    val status: String? = null
)

// ==========================================
// 2. REPOSITORY
// ==========================================

class OrganizationMemberRepository {

    // Cliente Genérico
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    /**
     * LISTAR MEMBROS
     * GET /members/organization/{id}
     */
    fun listMembers(
        organizationId: String,
        token: String,
        onSuccess: (List<OrganizationMember>) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.get(BASE_URL + "/members/organization/$organizationId") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                if (httpResponse.status.value >= 400) {
                    val errorBody = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $errorBody")
                    return@launch
                }

                val response = httpResponse.body<MemberListResponse>()

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

    // --- ADICIONADO: BUSCAR MEMBRO POR ID (READ SINGLE) ---
    /**
     * BUSCAR UM MEMBRO ESPECÍFICO
     * GET /members/{id}
     */
    fun getMemberById(
        memberId: String,
        token: String,
        onSuccess: (OrganizationMember) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.get(BASE_URL + "/members/$memberId") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                if (httpResponse.status.value >= 400) {
                    val errorBody = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $errorBody")
                    return@launch
                }

                val response = httpResponse.body<MemberResponse>()

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

    /**
     * ATUALIZAR MEMBRO (Role ou Status)
     * PUT /members/{id}
     */
    fun updateMember(
        memberId: String,
        newRole: String? = null,
        newStatus: String? = null,
        token: String,
        onSuccess: (OrganizationMember) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.put(BASE_URL + "/members/$memberId") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        MemberUpdateRequest(
                            role = newRole,
                            status = newStatus
                        )
                    )
                }

                if (httpResponse.status.value >= 400) {
                    val errorBody = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $errorBody")
                    return@launch
                }

                val response = httpResponse.body<MemberResponse>()

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

    /**
     * REMOVER MEMBRO
     * DELETE /members/{id}
     */
    fun deleteMember(
        memberId: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val httpResponse = client.delete(BASE_URL + "/members/$memberId") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                if (httpResponse.status.value >= 400) {
                    val errorBody = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $errorBody")
                    return@launch
                }

                // Delete geralmente retorna 200 OK
                onSuccess()

            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            }
        }
    }
}