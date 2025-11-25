// commonMain/kotlin/com/campusface/components/BottomBar.kt
package com.campusface.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.campusface.navigation.DashboardRoute // Assumindo o import das suas rotas

// 🚨 Reutiliza ou define os nomes de rota constantes para evitar a reflexão:
private object RouteNames {
    const val MEMBRO = "com.campusface.navigation.DashboardRoute.Membro"
    const val ADMINISTRAR = "com.campusface.navigation.DashboardRoute.Administrar"
    const val MEU_PERFIL = "com.campusface.navigation.DashboardRoute.MeuPerfil"

    const val VALIDAR = "com.campusface.navigation.DashboardRoute.Validar"
    // Adicione outras rotas da BottomBar se necessário

    const val SAIR =  "com.campusface.navigation.DashboardRoute.Sair"
}

// 1. Definição Estruturada dos Itens da Barra (Simplificada)
// Agora usamos a string constante (para seleção) e o objeto (para navegação)
private val bottomNavItems = listOf(
    Triple("Membro", DashboardRoute.Membro, RouteNames.MEMBRO),
    Triple("Admin", DashboardRoute.Administrar, RouteNames.ADMINISTRAR),
    Triple("Validar", DashboardRoute.Administrar, RouteNames.VALIDAR),
    Triple("Perfil", DashboardRoute.MeuPerfil, RouteNames.MEU_PERFIL),
    Triple("Sair", DashboardRoute.MeuPerfil, RouteNames.SAIR)
)

@Composable
fun BottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestinationRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        // Agora iteramos sobre Rótulo, Objeto de Rota e Nome Constante
        bottomNavItems.forEach { (label, routeObject, routeNameConstant) ->

            // 2. Lógica de Seleção: Compara a rota atual com a String Constante.
            val isSelected = currentDestinationRoute == routeNameConstant

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        // 3. Navegação Type-Safe com o objeto
                        navController.navigate(routeObject) {
                            navController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                popUpTo(startDestinationRoute) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { /* Item vazio */ },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}