// commonMain/kotlin/screens/AdicionarMembroScreen.kt
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


@Composable
fun AdicionarMembroScreen(
    onVoltarParaMembro: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth() // Ocupa toda a largura para o padding funcionar
                .padding(16.dp),
            // 🔑 1. Alinha todos os itens (Ícone e Texto) no centro vertical da Row
            verticalAlignment = Alignment.CenterVertically,
            // 🔑 2. Usa o SpaceBetween para forçar o Ícone para a esquerda e o Texto/Título
            // para a direita ou apenas Center se quiser o grupo centralizado.
            // Para um cabeçalho de tela, o Arrangement.Start é mais comum.
            horizontalArrangement = Arrangement.Start // Alinha os itens à esquerda
        ) {
            // IconButton já está alinhado na Row
            IconButton(
                onClick = onVoltarParaMembro
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar para a listagem"
                )
            }

            // Adiciona um espaçamento horizontal entre o ícone e o texto (opcional)
            Spacer(modifier = Modifier.width(8.dp))

            // O Texto está alinhado verticalmente com o ícone graças ao verticalAlignment
            Text("Entrar em um hub")

        }


        // ... (dentro do Box, após o IconButton, etc.)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .padding(horizontal = 32.dp),
            // 🔑 O ALINHAMENTO HORIZONTAL DO COLUMN ESTÁ CENTRALIZADO
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 🔑 MUDANÇA AQUI: Aplicar o alinhamento `Start` apenas ao Text
            Text(
                "Digite o código do hub",
                modifier = Modifier
                    .fillMaxWidth() // Ocupa a mesma largura do TextField abaixo
                    .align(Alignment.Start) // Força o alinhamento à esquerda (Start)
                    .padding(bottom = 8.dp) // Adiciona um pequeno espaço abaixo do rótulo
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
                    onVoltarParaMembro()
                },
                enabled = nome.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar entrada")
            }
        }
    }
}