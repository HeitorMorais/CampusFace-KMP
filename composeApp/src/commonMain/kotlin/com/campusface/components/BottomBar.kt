package com.campusface.components

// commonMain/kotlin/components/CampusfaceBottomBar.kt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.campusface.navigation.DashboardScreen // Assume que este é seu sealed class/enum

// Data class para estruturar os itens de navegação
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: DashboardScreen
)
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Membro",
        icon = Icons.Filled.People,
        screen = DashboardScreen.Membro
    ),
    BottomNavItem(
        label = "Administrar",
        icon = Icons.Filled.Settings,
        screen = DashboardScreen.Administrar
    ),
    BottomNavItem(
        label = "Validar",
        icon = Icons.Filled.CheckCircle,
        screen = DashboardScreen.Validar
    ),
    BottomNavItem(
        label = "Meu Perfil",
        icon = Icons.Filled.Person,
        screen = DashboardScreen.MeuPerfil
    )
)

@Composable
fun BottomBar(
    currentScreen: DashboardScreen,
    onScreenSelected: (DashboardScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    // NavigationBar é o contêiner Material 3 para a barra inferior
    NavigationBar(
        modifier = modifier
        // containerColor, contentColor, etc., são definidos pelo tema M3
    ) {
        // Itera sobre a lista de itens definidos acima
        bottomNavItems.forEach { item ->
            // NavigationBarItem é o item de navegação individual
            val isSelected = currentScreen::class == item.screen::class

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onScreenSelected(item.screen) }
                // Cores do item são tratadas automaticamente pelo M3
            )
        }
    }
}