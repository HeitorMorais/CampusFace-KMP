package com.campusface.screens.administrarScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.campusface.data.Hub
import com.campusface.data.hubsList
import com.campusface.navigation.DashboardRoute


@Composable
fun HubCard(
    hub: Hub,
    onHubClick: (String) -> Unit
) {
    Card(
        onClick = { onHubClick(hub.id.toString()) },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = hub.nome)

            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("group")
                val quantidade = hub.quantidadeMembros ?: 0
                Text(text = "Membros: $quantidade")
            }
        }
    }
}


@Composable
fun AdministrarScreen(navController: NavHostController) {

    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Text("Hubs que administro", fontSize = 24.sp)

            Button(
                onClick = {
                    // 🔑 AÇÃO: Navega para AdicionarHub (usando a rota objeto)
                    navController.navigate(DashboardRoute.AdicionarMembro) // Assumindo que você renomeou AdicionarMembro para AdicionarHub
                },
            ) {
                Text("Criar")
            }
        }

        // --- LISTA DE HUBS ---
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(hubsList) { hubAtual ->
                HubCard(
                    hub = hubAtual,
                    // 🚀 NAVEGAÇÃO TYPE-SAFE APLICADA AQUI
                    onHubClick = { hubId ->
                        // Chamada direta, usando a classe de dados
                        navController.navigate(
                            DashboardRoute.DetalhesHub(hubId = hubId)
                        )
                    }
                )
            }
        }
    }
}