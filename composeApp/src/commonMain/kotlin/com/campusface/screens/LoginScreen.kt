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
) {
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()

    var username by remember { mutableStateOf("usuario_teste") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {



        Button(
            onClick = { authRepository.login(username)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Entrar")
        }
    }
}

//