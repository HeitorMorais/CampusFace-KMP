// commonMain/kotlin/DashboardScreen.kt
package com.campusface.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.campusface.components.BottomBar

import com.campusface.navigation.DashboardScreen
import com.campusface.components.Sidebar
import com.campusface.components.MainContent

@Composable
fun DashboardScreen() {
    var currentScreen by remember { mutableStateOf<DashboardScreen>(DashboardScreen.Membro) }
    val onNavigate: (DashboardScreen) -> Unit = { newScreen ->
        currentScreen = newScreen
    }
    UrlSyncEffect(
        currentScreen = currentScreen,
        onScreenChangedByUrl = { screen ->
            currentScreen = screen
        }
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        val isMobile = maxWidth < 600.dp

        if (isMobile) {
            Scaffold(
                bottomBar = {
                    BottomBar(
                        currentScreen = currentScreen,
                        onScreenSelected = onNavigate
                    )
                }
            ) {
                MainContent(
                    currentScreen = currentScreen,
                    onNavigate = onNavigate
                )
            }
        } else {
            // Layout Expandido (Desktop/Tablet): Sidebar Fixo com Row
            Row(modifier = Modifier.fillMaxSize()) {
                // Sidebar (Barra de Navegação Vertical)
                Sidebar(
                    currentScreen = currentScreen,
                    onScreenSelected = onNavigate
                )

                // Main Content (Ocupa o restante do espaço)
                MainContent(
                    currentScreen = currentScreen,
                    onNavigate = onNavigate
                )
            }
        }
    }
}



@Composable
expect fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
)