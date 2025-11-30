package com.campusface.data.Repository

import com.campusface.data.BASE_URL
import com.campusface.data.Model.User // Certifique-se de que sua classe User existe neste pacote
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


@Serializable
data class OrganizationMember(
    val id: String,
    val role: String,   // Ex: "MEMBER", "ADMIN", "VALIDATOR"
    val status: String, // Ex: "PENDING", "ACTIVE"
    val joinedAt: String? = null,
    val user: User      // Dados do usuário (nome, email, foto)
)

@Serializable
data class MemberListResponse(
    val success: Boolean,
    val message: String,
    val data: List<OrganizationMember> = emptyList()
)


class OrganizationMemberRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }


    fun listMembers(
        organizationId: String,
        token: String,
        onSuccess: (List<OrganizationMember>) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("Buscando membros da Org: $organizationId")

                val httpResponse = client.get(BASE_URL + "/members/organization/$organizationId") {
                    headers {
                        // Header necessário para passar pela tela de aviso do Ngrok (se estiver usando versão free)
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                }

                // Verifica erros HTTP
                if (httpResponse.status.value >= 400) {
                    val errorBody = httpResponse.bodyAsText()
                    println("Erro API: $errorBody")
                    onError("Erro ${httpResponse.status.value}: Falha ao buscar membros.")
                    return@launch
                }

                // Converte o JSON
                val response = httpResponse.body<MemberListResponse>()

                if (response.success) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                println("ListMembers ERROR: ${e.message}")
                e.printStackTrace()
                onError("Erro de conexão: ${e.message}")
            }
        }
    }
}