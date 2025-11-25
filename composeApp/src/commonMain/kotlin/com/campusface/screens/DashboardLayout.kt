// commonMain/kotlin/com/campusface/screens/DashboardLayout.kt
package com.campusface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute // 🚨 IMPORTANTE
import com.campusface.components.BottomBar
import androidx.compose.runtime.getValue // IMPORTANTE: Para o delegado 'by'
import androidx.navigation.compose.currentBackStackEntryAsState // IMPORTANTE: Para a função de estado
import com.campusface.components.Sidebar
import com.campusface.navigation.DashboardRoute // 🚨 Nova Rota
import com.campusface.screens.membroScreen.MembroScreen
import com.campusface.screens.membroScreen.AdicionarMembroScreen
import com.campusface.screens.administrarScreen.AdministrarScreen
import com.campusface.screens.administrarScreen.DetalhesHubScreen

@Composable
fun DashboardLayout(
    navController: NavHostController
) {
    // 1. 🧭 OBSERVAR O ESTADO DE NAVEGAÇÃO
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Convertemos a rota para a classe Type-Safe para passar para as barras
    // Se a rota não for Type-Safe ou for nula, usamos null
    val currentDestination = backStackEntry?.destination?.route // Rota bruta da URL

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isMobile = maxWidth < 600.dp

        if (isMobile) {
            // =========================
            // Layout Mobile (BottomBar)
            // =========================
            Scaffold(
                bottomBar = {
                    // 3. 📱 RENDERIZA A BARRA INFERIOR
                    BottomBar(
                        navController = navController // Passa a rota atual para destaque
                    )
                }
            ) { paddingValues ->
                // 4. 🔀 CONTEÚDO: Aplica o padding do Scaffold ao NavHost
                DashboardContentNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        } else {
            // ===========================
            // Layout Desktop/Tablet (Sidebar)
            // ===========================
            Row(modifier = Modifier.fillMaxSize()) {
                // 3. 💻 RENDERIZA A BARRA LATERAL
                Sidebar(
                    navController = navController,
                )

                // 4. 🔀 CONTEÚDO: Ocupa o restante da largura
                DashboardContentNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize().weight(1f) // Ocupa o espaço restante
                )
            }
        }
    }
}
@Composable
fun DashboardContentNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute.Membro,
        modifier = modifier
    ) {
        // Rotas Simples
        composable<DashboardRoute.Membro> {
            MembroScreen(navController = navController)
        }

        composable<DashboardRoute.AdicionarMembro> {
            AdicionarMembroScreen(navController = navController)
        }

        composable<DashboardRoute.Administrar> {
            AdministrarScreen(navController = navController)
        }

        composable<DashboardRoute.Validar> {
            ValidarScreen()
        }

        composable<DashboardRoute.MeuPerfil> {
            MeuPerfilScreen()
        }

        // 🏆 ROTA COM ARGUMENTO (Type-Safe)
        composable<DashboardRoute.DetalhesHub> { backStackEntry ->
            // 🚀 Recupera o objeto com os dados tipados
            val rota = backStackEntry.toRoute<DashboardRoute.DetalhesHub>()

            DetalhesHubScreen(
                hubId = rota.hubId, // Acessa direto da classe
                navController = navController
            )
        }
    }
}