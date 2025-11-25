package com.campusface.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 🔑 MUDANÇA: Usamos sealed interface e marcamos cada rota com @Serializable
@Serializable
sealed interface AppRoute {

    // 1. Rota de Login (Não precisa de argumentos)
    @Serializable
    @SerialName("login")
    data object Login : AppRoute

    // 2. Rota Principal do Dashboard (Não precisa de argumentos no nível superior)
    @Serializable
    @SerialName("dashboard")
    data object DashboardGraph : AppRoute

    @Serializable
    data object Splash : AppRoute

    // Se fosse necessário passar um ID de usuário após o login:
    // @Serializable
    // data class DashboardGraph(val userId: String) : AppRoute
}