package com.campusface.screens

// commonMain/kotlin/screens/SettingsScreen.kt
// (Código similar para a tela de Settings)
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Dashboard: Configurações", fontSize = 30.sp)
    }
}