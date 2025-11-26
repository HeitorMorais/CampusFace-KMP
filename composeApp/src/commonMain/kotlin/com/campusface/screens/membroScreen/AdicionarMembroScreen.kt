package com.campusface.screens.membroScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box // 🆕 Importado
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons // 🆕 Importado
import androidx.compose.material.icons.automirrored.filled.ArrowBack // 🆕 Importado para a seta
import androidx.compose.material3.Button
import androidx.compose.material3.Icon // 🆕 Importado
import androidx.compose.material3.IconButton // 🆕 Importado
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer


@Composable
fun AdicionarMembroScreen(
    navController: NavHostController
) {
    var nome by remember { mutableStateOf("") }
    AdaptiveScreenContainer(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar para a listagem"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("Entrar em um hub", style = MaterialTheme.typography.titleMedium)
            }
        }

        // formulario
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Digite o código do hub",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Ex: 123ABC") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    // 🎯 AÇÃO: No sucesso, volta para a tela anterior
                    // Aqui você chamaria a ViewModel para a lógica de "Solicitar Entrada"
                    // Por enquanto, apenas simula o retorno
                    navController.popBackStack()
                },
                enabled = nome.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar entrada")
            }
        }
    }}
}