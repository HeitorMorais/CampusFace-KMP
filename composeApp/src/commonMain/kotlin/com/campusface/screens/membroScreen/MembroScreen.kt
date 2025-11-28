package com.campusface.screens.membroScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.navigation.DashboardRoute // Importa suas rotas de dashboard
import com.campusface.data.Model.Hub // Importa o modelo Hub
import com.campusface.data.Model.hubsList

@Composable
fun StatusCircle(tamanho: Dp = 12.dp, color: Color) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .background(color = color, shape = CircleShape)
    )
}

@Composable
fun HubCard(hub : Hub, navController: NavHostController, isValidator: Boolean) {
    val statusColor = when (hub.status?.lowercase()) {
        "ativo" -> Color(0xFF00A12B) // verde
        "solicitado" -> Color(0xFFFFBB00) // amarelo
        else -> Color.Gray
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth().
            padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = { navController.navigate(if(isValidator) DashboardRoute.QrCodeValidador else DashboardRoute.QrCodeMembro) }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(33.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = hub.nome, style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusCircle(color = statusColor, tamanho = 10.dp)
                hub.status?.let { Text(text = it, style=MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)}
            }
        }
    }
}

@Composable
fun HubListCard(navController: NavHostController, isValidator: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(hubsList) { hubAtual ->
            HubCard(hub = hubAtual, navController = navController, isValidator = isValidator)
        }
    }
}
@Composable
fun MembroScreen(navController: NavHostController) {
    AdaptiveScreenContainer(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Center

        ) {
            Text("Hubs que sou membro", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                    navController.navigate(DashboardRoute.AdicionarMembro)
                },
                modifier = Modifier
            ) {
                Text("Adicionar", style=MaterialTheme.typography.labelMedium)
            }
        }

        HubListCard(navController = navController, isValidator = false)
    }
    }
}