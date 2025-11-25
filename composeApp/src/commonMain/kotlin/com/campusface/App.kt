package com.campusface
import androidx.compose.runtime.*
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
    // 1. Instancia o Repositório (Em app real, use Koin/DI)
    val authRepository = remember { AuthRepository() }

    // 2. Provider do Auth
    CompositionLocalProvider(LocalAuthRepository provides authRepository) {

        // 3. Controle de Navegação e Estado
        val navController = rememberNavController()
        val authState by authRepository.authState.collectAsState()

        // Redirecionamento Inteligente
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
        // 4. NavHost Raiz (Switch entre Login e o "Mundo Dashboard")
        NavHost(
            navController = navController,
            startDestination = if (authState.isAuthenticated) AppRoute.DashboardGraph else AppRoute.Login
        ) {
            // Rota de Login (Tela cheia)
            composable<AppRoute.Login> {
                LoginScreen()
            }

            composable<AppRoute.DashboardGraph> {
                // Criamos um controller separado para a navegação INTERNA do dashboard
                // Isso isola a navegação lateral da navegação de Login/Logout
                val dashboardNavController = rememberNavController()

                DashboardLayout(navController = dashboardNavController)
            }
        }
    }
    }
}