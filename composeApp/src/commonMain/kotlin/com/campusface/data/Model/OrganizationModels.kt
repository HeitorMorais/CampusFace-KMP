package com.campusface.data.Model

import kotlinx.serialization.Serializable

// O corpo que enviamos para criar a organização
@Serializable
data class OrganizationCreateRequest(
    val name: String,
    val description: String,
    val hubCode: String
)

// A estrutura da Organização que volta do servidor
@Serializable
data class Organization(
    val id: String,
    val name: String,
    val description: String,
    val hubCode: String,
    // As listas de usuários (User deve estar definido no seu projeto)
    val admins: List<User> = emptyList(),
    val validators: List<User> = emptyList(),
    val members: List<User> = emptyList()
)

// O envelope de resposta da API
@Serializable
data class OrganizationResponse(
    val success: Boolean,
    val message: String,
    val data: Organization? = null
)