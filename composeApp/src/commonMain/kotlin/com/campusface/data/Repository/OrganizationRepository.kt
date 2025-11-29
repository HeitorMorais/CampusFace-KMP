package com.campusface.data.Repository

import com.campusface.data.BASE_URL
import com.campusface.data.Model.Organization
import com.campusface.data.Model.OrganizationCreateRequest
import com.campusface.data.Model.OrganizationResponse
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
import kotlinx.serialization.json.Json

class OrganizationRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Importante para ignorar campos extras como 'createdAt' se não mapeados
                prettyPrint = true
                isLenient = true
            })
        }
    }

    fun createOrganization(
        name: String,
        description: String,
        hubCode: String,
        token: String?,
        onSuccess: (Organization) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("Criando Org: $name ($hubCode)")

                val httpResponse = client.post(BASE_URL + "/organizations") {
                    headers {
                        append("ngrok-skip-browser-warning", "true")
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        OrganizationCreateRequest(
                            name = name,
                            description = description,
                            hubCode = hubCode
                        )
                    )
                }

                // Verifica erros HTTP (400, 401, 403, 500)
                if (httpResponse.status.value >= 400) {
                    val errorBody = httpResponse.bodyAsText()
                    onError("Erro ${httpResponse.status.value}: $errorBody")
                    return@launch
                }

                // Tenta deserializar o JSON
                val response = httpResponse.body<OrganizationResponse>()

                if (response.success && response.data != null) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                println("CreateOrganization ERROR: ${e.message}")
                e.printStackTrace()
                onError("Erro de conexão: ${e.message}")
            }
        }
    }
}