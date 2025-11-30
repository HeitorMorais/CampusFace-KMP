package com.campusface.data.Repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*  // 👈 ADICIONA
import io.ktor.serialization.kotlinx.json.*  // 👈 ADICIONA
import io.ktor.http.*
import com.campusface.data.Model.User
import com.campusface.data.Model.ApiResponse
import com.campusface.data.BASE_URL
import kotlinx.serialization.json.Json  // 👈 ADICIONA

class UserRepository(private val token: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }

    suspend fun getUser(id: String): Result<User> = runCatching {
        val httpResponse: HttpResponse = client.get("${BASE_URL}/users/$id") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header("ngrok-skip-browser-warning", "true")
        }

        val rawBody = httpResponse.bodyAsText()
        println("🔍 DEBUG: Status Code: ${httpResponse.status}")
        println("🔍 DEBUG: Resposta da API: $rawBody")

        val response: ApiResponse<User> = httpResponse.body()
        response.data ?: throw Exception("Usuário não encontrado")
    }

    suspend fun updateProfileImage(id: String, imageBytes: ByteArray): Result<Unit> = runCatching {
        client.submitFormWithBinaryData(
            url = "${BASE_URL}/users/$id/image",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=profile.jpg")
                })
            }
        ) {
            method = HttpMethod.Patch
            header(HttpHeaders.Authorization, "Bearer $token")
            header("ngrok-skip-browser-warning", "true")
        }
    }

    suspend fun deleteUser(id: String): Result<Unit> = runCatching {
        client.delete("${BASE_URL}/users/$id") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header("ngrok-skip-browser-warning", "true")
        }
    }
}