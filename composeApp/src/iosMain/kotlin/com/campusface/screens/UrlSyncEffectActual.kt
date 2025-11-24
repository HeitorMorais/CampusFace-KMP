package com.campusface.screens

import androidx.compose.runtime.Composable
// Importe a sua classe DashboardScreen aqui, se necessário:
import com.campusface.navigation.DashboardScreen

// Esta é a implementação 'actual' para o target iOS/Native.
@Composable
actual fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
) {
    // No iOS, não manipulamos o URL fragment (#) para navegação interna,
    // então a implementação é vazia (no-op).
}