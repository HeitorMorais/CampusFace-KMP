package com.campusface.data.Model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val hashedPassword: String? = null, // <- ❗ AGORA NÃO CAUSA ERRO
    val document: String = "",
    val faceImageId: String? = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
