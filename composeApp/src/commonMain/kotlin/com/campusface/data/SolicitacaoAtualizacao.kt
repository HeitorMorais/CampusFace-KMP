package com.campusface.data


data class SolicitacaoAtualizacao(
    val id: String,
    val hubId: Int,
    val solicitante: Usuario,
    val fotoAntigaUrl: String,
    val fotoNovaUrl: String,
    val dataSolicitacao: String
)