package com.campusface.screens.membroScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusface.data.Hub

val hubsList = listOf(
    Hub(1, "Fatec Zona Leste", "ativo"),
    Hub(2, "Fatec Itaquera", "solicitado"),
    Hub(3, "Fatec São Paulo", "solicitado")
)

@Composable
fun StatusCircle(tamanho: Dp = 12.dp, color: Color) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .background(color = color, shape = CircleShape)
    )
}


@Composable
fun HubCard(hub : Hub) {
    val statusColor = when (hub.status?.lowercase()) {
        "ativo" -> Color.Green
        "solicitado" -> Color.Yellow

        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hub.nome
            )

            Row(modifier = Modifier
                .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusCircle(color = statusColor, tamanho = 10.dp)
                hub.status?.let { Text(text = it) }
            }
        }
    }

}
@Composable
fun HubListCard(
) {
    LazyColumn {
        items(hubsList) { hubAtual ->
            HubCard(hub = hubAtual)
        }
    }
}
@Composable
fun MembroScreen(onAdicionarClick: () -> Unit) {
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.
            padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("Hubs que sou membro", fontSize = 30.sp)
            // 🔑 O BOTÃO "ADICIONAR MEMBRO"
            Button(
                onClick = onAdicionarClick,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text("Adicionar Membro")
            }
        }
        HubListCard()
    }
}