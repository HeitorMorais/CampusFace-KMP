package com.campusface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.campusface.components.BottomBar
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.campusface.components.Sidebar
import com.campusface.navigation.DashboardRoute
import com.campusface.screens.membroScreen.MembroScreen
import com.campusface.screens.membroScreen.AdicionarMembroScreen
import com.campusface.screens.administrarScreen.AdministrarScreen
import com.campusface.screens.administrarScreen.DetalhesHubScreen
import com.campusface.screens.membroScreen.QrCodeMembroScreen
import com.campusface.screens.membroScreen.ValidarScreen
import com.campusface.screens.validarScreen.QrCodeValidadorScreen

@Composable
fun DashboardLayout(
    navController: NavHostController
    // 👈 NÃO PRECISA MAIS DE userId NEM userRepository
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isMobile = maxWidth < 600.dp

        if (isMobile) {
            Scaffold(
                bottomBar = {
                    BottomBar(navController = navController)
                }
            ) { paddingValues ->
                DashboardContentNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Sidebar(navController = navController)

                DashboardContentNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxWidth().widthIn(650.dp)
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
            ValidarScreen(navController = navController)
        }

        composable<DashboardRoute.MeuPerfil> {
            MeuPerfilScreen()  // 👈 NÃO PRECISA PASSAR NADA
        }

        composable<DashboardRoute.DetalhesHub> { backStackEntry ->
            val rota = backStackEntry.toRoute<DashboardRoute.DetalhesHub>()
            DetalhesHubScreen(
                hubId = rota.hubId,
                navController = navController
            )
        }

        composable<DashboardRoute.QrCodeMembro> { backStackEntry ->
            QrCodeMembroScreen(navController = navController)
        }

        composable<DashboardRoute.QrCodeValidador> { backStackEntry ->
            QrCodeValidadorScreen(navController = navController)
        }
    }
}