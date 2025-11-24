package com.campusface.navigation

import androidx.compose.runtime.Immutable

@Immutable
sealed class DashboardScreen {
    data object Membro : DashboardScreen()
    data object AdicionarMembro : DashboardScreen()

    data object Administrar : DashboardScreen()
    data class DetalhesHub(val hubId: String) : DashboardScreen()

    data object Validar : DashboardScreen()

    data object MeuPerfil : DashboardScreen()
}