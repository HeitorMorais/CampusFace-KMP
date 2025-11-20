package com.campusface.screens

// jvmMain/kotlin/UrlSyncEffectActual.kt

import androidx.compose.runtime.Composable
import com.campusface.navigation.DashboardScreen // Ajuste este import conforme o seu projeto

// A implementação 'actual' para Desktop (JVM) é vazia.
@Composable
actual fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
) {
    // Não é necessário sincronizar a URL no Desktop.
}