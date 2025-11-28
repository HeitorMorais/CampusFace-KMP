package com.campusface.data.Model

// Módulo: shared/src/commonMain/kotlin/data/Usuario.kt

data class Usuario(
    val id: String, // Identificador único do usuário (geralmente obrigatório)
    val nome: String,
    val cpf: String,
    val email: String,
    val fotoPerfilUrl: String
)
