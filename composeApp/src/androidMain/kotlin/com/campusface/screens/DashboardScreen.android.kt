package com.campusface.screens

import androidx.compose.runtime.Composable
import com.campusface.navigation.DashboardScreen

@Composable
actual fun UrlSyncEffect(
    currentScreen: DashboardScreen,
    onScreenChangedByUrl: (DashboardScreen) -> Unit
) {
}