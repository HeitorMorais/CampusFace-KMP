package com.campusface.components

// commonMain/kotlin/components/Sidebar.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusface.navigation.DashboardScreen

@Composable
fun Sidebar(
    currentScreen: DashboardScreen,
    onScreenSelected: (DashboardScreen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Campus Face", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))

        SidebarItem(
            label = "Membro",
            isSelected = currentScreen is DashboardScreen.Membro,
            onClick = { onScreenSelected(DashboardScreen.Membro) }
        )
        Spacer(Modifier.height(8.dp))

        SidebarItem(
            label = "Administrar",
            isSelected = currentScreen is DashboardScreen.Administrar,
            onClick = { onScreenSelected(DashboardScreen.Administrar) }
        )
        Spacer(Modifier.height(8.dp))

        SidebarItem(
            label = "Validar",
            isSelected = currentScreen is DashboardScreen.Validar,
            onClick = { onScreenSelected(DashboardScreen.Validar) }
        )
        Spacer(Modifier.height(8.dp))

        SidebarItem(
            label = "Meu Perfil",
            isSelected = currentScreen is DashboardScreen.MeuPerfil,
            onClick = { onScreenSelected(DashboardScreen.MeuPerfil) }
        )
    }
}

@Composable
fun SidebarItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White else Color.Transparent
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Usa Box para centralizar o texto no espaço do botão
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(
                text = label,
                color = if (isSelected) Color.Black else Color.DarkGray
            )
        }
    }
}