package com.campusface.data

data class Hub(
    val id: Int, // OBRIGATÓRIO (sem valor padrão)

    // Opcionais: Definindo 'null' como valor padrão
    val nome: String,
    val status: String? = null,
    val quantidadeMembros: Int? = null,
    val temIconeGrupo: Boolean? = true // Note que este já tinha um padrão
)
