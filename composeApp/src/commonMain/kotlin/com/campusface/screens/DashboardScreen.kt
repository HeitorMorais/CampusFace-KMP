// commonMain/kotlin/DashboardScreen.kt
package com.campusface.screens
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

// Importe sua classe de navegação
import com.campusface.navigation.DashboardScreen // Ajuste o pacote conforme o seu projeto

// Importe seus componentes
import com.campusface.components.Sidebar
import com.campusface.components.MainContent

// =======================================================
// 1. FUNÇÃO COMPOSABLE PRINCIPAL (Mantém o Estado)
// =======================================================

@Composable
fun DashboardScreen() {
    // Gerenciamento de Estado: Qual tela está selecionada
    var currentScreen by remember { mutableStateOf<DashboardScreen>(DashboardScreen.Overview) }

    // Efeito de Sincronização de URL (EXPECT/ACTUAL)
    // Chama a função que será implementada especificamente para Web (com lógica de URL)
    // e para Desktop/Mobile (como uma função vazia).
    UrlSyncEffect(
        currentScreen = currentScreen,
        onScreenChangedByUrl = { screen ->
            currentScreen = screen
        }
    )

    // Estrutura de Layout: Sidebar (esquerda) e Conteúdo (direita)
    Row(modifier = Modifier.fillMaxSize()) {

        // Sidebar Fixo: Atualiza o estado ao clicar
        Sidebar(
            currentScreen = currentScreen,
            onScreenSelected = { newScreen ->
                currentScreen = newScreen
            }
        )

        // Main Content (Outlet): Renderiza a tela baseada no estado
        MainContent(
            currentScreen = currentScreen
        )
    }
}

// =======================================================
// 2. DECLARAÇÃO EXPECT (Contrato Multiplatform)
//    DEVE ESTAR NO NÍVEL SUPERIOR
// =======================================================

@Composable
expect fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
)