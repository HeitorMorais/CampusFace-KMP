// commonMain/kotlin/com/campusface/screens/LoginScreen.kt
package com.campusface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusface.auth.LocalAuthRepository // Importa o nosso Provider
import com.campusface.auth.AuthRepository

@Composable
fun LoginScreen(
    // Não precisamos de navController aqui, pois a navegação é tratada
    // pela observação do AuthState no App.kt
) {
    // 1. Acesso ao AuthRepository via CompositionLocal
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()

    var username by remember { mutableStateOf("usuario_teste") }

    Column(
        modifier = Modifier
            .fillMaxSize() // O Column deve ocupar a largura máxima
            .padding(24.dp),
        // 🔑 CHAVE AQUI: Alinhamento Horizontal
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Opcional: Centraliza verticalmente o conteúdo
    ) {
        // ... (Componentes de Email, Senha, etc.)

        // 🎯 O BOTÃO VAI HERDAR O ALINHAMENTO DO PAI
        Button(
            onClick = { authRepository.login(username)},
            modifier = Modifier
                .fillMaxWidth() // Opcional: faz o botão ocupar toda a largura disponível
                .padding(top = 16.dp)
        ) {
            Text("Entrar")
        }
    }
}

//