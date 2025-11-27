// commonMain/kotlin/com/campusface/components/BottomBar.kt
package com.campusface.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.campusface.navigation.DashboardRoute // Assumindo o import das suas rotas
import com.campusface.navigation.DashboardRouteNames

private val railItems = listOf(
    RailItem(
        label = "Membro",
        route = DashboardRouteNames.MEMBRO,
        icon = Icons.Filled.Home, // Exemplo
        destination = DashboardRoute.Membro
    ),
    RailItem(
        label = "Admin",
        route = DashboardRouteNames.ADMINISTRAR,
        icon = Icons.Filled.Settings, // Exemplo
        destination = DashboardRoute.Administrar
    ),
    RailItem(
        label = "Validar",
        route = DashboardRouteNames.VALIDAR,
        icon = Icons.Filled.Check, // Exemplo
        destination = DashboardRoute.Validar
    ),
    RailItem(
        label = "Perfil",
        route = DashboardRouteNames.MEU_PERFIL,
        icon = Icons.Filled.Person, // Exemplo
        destination = DashboardRoute.MeuPerfil
    ),
//    RailItem(
//        label = "Sair",
//        route = DashboardRouteNames.SAIR,
//        icon = Icons.Filled.ExitToApp, // Exemplo
//        destination = DashboardRoute.Sair
//    ),
)


@Composable
fun BottomBar(
    navController: NavHostController
) {
    val cores = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.background,   // Muda o texto selecionado
        indicatorColor = MaterialTheme.colorScheme.primary, // Muda o fundo/indicador selecionado

        unselectedIconColor = MaterialTheme.colorScheme.primary, // Muda o ícone não-selecionado
        unselectedTextColor = MaterialTheme.colorScheme.primary,   // Muda o texto não-selecionado
    )
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRouteName = backStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.padding(5.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        // Agora iteramos sobre Rótulo, Objeto de Rota e Nome Constante
        railItems.forEach { item ->

            // 2. Lógica de Seleção: Compara a rota atual com a String Constante.
            val isSelected = currentRouteName == item.route

            NavigationBarItem(
                colors = cores,
                modifier = Modifier.padding(10.dp),
                selected = isSelected,
                onClick = {

                    if (!isSelected) {
                        navController.navigate(item.destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, color=MaterialTheme.colorScheme.primary, fontSize = 14.sp) },
            )
        }
        val isSelectedSair = currentRouteName == DashboardRouteNames.SAIR
        NavigationBarItem(
            colors = cores,
            modifier = Modifier.weight(1f),
            selected = isSelectedSair,
            onClick = {

                if (!isSelectedSair) {
                    navController.navigate(DashboardRoute.Sair) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    Icons.Filled.ExitToApp,
                    contentDescription = "Sair"
                )
            },
            label = { Text("Sair", fontSize =14.sp) },
        )
    }
}