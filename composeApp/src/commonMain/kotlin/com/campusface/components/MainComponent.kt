package com.campusface.components

// commonMain/kotlin/components/MainContent.kt

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.campusface.navigation.DashboardScreen

import com.campusface.screens.OverviewScreen
import com.campusface.screens.SettingsScreen
import com.campusface.screens.ReportsScreen

@Composable
fun MainContent(currentScreen: DashboardScreen) {
    // Crossfade adiciona uma transição suave entre as telas
    Crossfade(targetState = currentScreen, label = "Dashboard Screen Transition", modifier = Modifier.fillMaxSize()) { screen ->
        when (screen) {
            is DashboardScreen.Overview -> OverviewScreen()
            is DashboardScreen.Settings -> SettingsScreen()
            // Passa o argumento da rota Reports para a tela ReportsScreen
            is DashboardScreen.Reports -> ReportsScreen(screen.reportId)
        }
    }
}