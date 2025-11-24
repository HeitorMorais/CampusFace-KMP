package com.campusface.screens.administrarScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import com.campusface.data.hubsList


@Composable
fun HubCard(
    hub: Hub,
    onHubClick: (Hub) -> Unit
) {
    Card(
        onClick = { onHubClick(hub) },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
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

            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("group")
                val quantidade = hub.quantidadeMembros ?: 0
                Text(text = "Membros: $quantidade")
            }
        }
    }
}
@Composable
fun AdministrarScreen(
    // Recebe o callback para navegar ao clicar no Hub
    onHubClick: (Hub) -> Unit,
    onCriarClick: () -> Unit
) {
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Hubs que administro", fontSize = 30.sp)

            Button(
                onClick = onCriarClick,
            ) {
                Text("Criar")
            }
        }

        // --- A LISTA AGORA ESTÁ AQUI ---
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(hubsList) { hubAtual ->
                // Passa a função onHubClick recebida pela AdministrarScreen
                HubCard(
                    hub = hubAtual,
                    onHubClick = onHubClick
                )
            }
        }
    }
}