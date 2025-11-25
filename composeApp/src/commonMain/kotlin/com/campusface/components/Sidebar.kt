// commonMain/kotlin/com/campusface/components/Sidebar.kt
package com.campusface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.campusface.navigation.DashboardRoute
import com.campusface.navigation.DashboardRouteNames
// 🚨 Define os nomes completos das rotas como strings literais para evitar Reflexão no Wasm

@Composable
fun Sidebar(
    navController: NavHostController // Recebe apenas o NavController
) {
    // 1. 🧭 OBTÉM A ROTA ATUAL INTERNAMENTE
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = backStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // --- Membro (Rota principal) ---
        SidebarItem(
            label = "Membro",
            // 🔑 VERIFICAÇÃO: Compara com a string literal
            isSelected = currentRouteName == DashboardRouteNames.MEMBRO,
            onClick = { navController.navigate(DashboardRoute.Membro) }
        )
        Spacer(Modifier.height(8.dp))

        // --- Administrar ---
        SidebarItem(
            label = "Administrar",
            isSelected = currentRouteName == DashboardRouteNames.ADMINISTRAR,
            onClick = { navController.navigate(DashboardRoute.Administrar) }
        )
        Spacer(Modifier.height(8.dp))

        // --- Validar ---
        SidebarItem(
            label = "Validar",
            isSelected = currentRouteName == DashboardRouteNames.VALIDAR,
            onClick = { navController.navigate(DashboardRoute.Validar) }
        )
        Spacer(Modifier.height(8.dp))

        // --- Meu Perfil ---
        SidebarItem(
            label = "Meu Perfil",
            isSelected = currentRouteName == DashboardRouteNames.MEU_PERFIL,
            onClick = { navController.navigate(DashboardRoute.MeuPerfil) }
        )

        SidebarItem(
            label = "Sair",
            isSelected = currentRouteName == DashboardRouteNames.SAIR,
            onClick = { navController.navigate(DashboardRoute.Sair) }
        )
    }
}

@Composable
fun SidebarItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray

    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(8.dp))

            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}