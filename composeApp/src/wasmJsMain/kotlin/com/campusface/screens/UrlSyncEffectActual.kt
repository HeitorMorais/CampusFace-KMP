package com.campusface.screens

// wasmJsMain/kotlin/UrlSyncEffectActual.kt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.campusface.navigation.DashboardScreen // Ajuste este import
import kotlinx.browser.window // <<< ESTE IMPORT SÓ EXISTE AQUI!

@Composable
actual fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
) {
    // 1. Lógica para ATUALIZAR a URL quando o estado interno muda
    LaunchedEffect(currentScreen) {
        val path = when (currentScreen) {
            DashboardScreen.Overview -> "overview"
            DashboardScreen.Settings -> "settings"
            is DashboardScreen.Reports -> "reports/${currentScreen.reportId}"
        }
        window.history.pushState(null, "", "#$path")
    }

    // 2. Lógica para LER a URL quando a página carrega ou o histórico muda
    LaunchedEffect(Unit) {
        fun parsePath(path: String): DashboardScreen {
            return when {
                path.contains("settings") -> DashboardScreen.Settings
                path.contains("reports") -> {
                    val reportId = path.substringAfter("reports/")
                    DashboardScreen.Reports(reportId)
                }
                else -> DashboardScreen.Overview
            }
        }

        // Leitura inicial
        val initialPath = window.location.hash.substringAfter("#")
        onScreenChangedByUrl(parsePath(initialPath))

        // Leitura do botão Back/Forward
        window.onpopstate = {
            val newPath = window.location.hash.substringAfter("#")
            onScreenChangedByUrl(parsePath(newPath))
        }
    }
}