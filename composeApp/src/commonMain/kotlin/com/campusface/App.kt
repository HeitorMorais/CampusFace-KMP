package com.campusface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.campusface.auth.AuthRepository
import com.campusface.auth.LocalAuthRepository
import com.campusface.navigation.AppRoute
import com.campusface.screens.DashboardLayout
import com.campusface.screens.LoginScreen
import com.campusface.theme.CampusFaceTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavHostController) -> Unit = {}
) {
    CampusFaceTheme {
    val authRepository = remember { AuthRepository() }


        CompositionLocalProvider(LocalAuthRepository provides authRepository) {

            val navController = rememberNavController()
            val authState by authRepository.authState.collectAsState()


            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(AppRoute.DashboardGraph) {
                        popUpTo(AppRoute.Login) { inclusive = true }
                    }
                } else {
                    navController.navigate(AppRoute.Login) {
                        popUpTo(AppRoute.DashboardGraph) { inclusive = true }
                    }
                }
            }
            LaunchedEffect(navController) {
                onNavHostReady(navController)
            }
            NavHost(
                navController = navController,
                startDestination = if (authState.isAuthenticated) AppRoute.DashboardGraph else AppRoute.Login
            ) {
                composable<AppRoute.Login> {
                    LoginScreen()
                }

                composable<AppRoute.DashboardGraph> {
                    // 🔑 CHAVE AQUI: Proteção contra manipulação de URL
                    if (authState.isAuthenticated) {
                        // Se estiver autenticado, carrega o conteúdo
                        val dashboardNavController = rememberNavController()
                        DashboardLayout(navController = dashboardNavController)
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(AppRoute.Login) {
                                popUpTo(AppRoute.DashboardGraph) { inclusive = true }
                            }
                        }

                        Box(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}