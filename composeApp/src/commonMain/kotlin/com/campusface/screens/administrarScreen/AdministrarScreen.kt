package com.campusface.screens.administrarScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Model.Hub
import com.campusface.data.Model.hubsList
import com.campusface.navigation.DashboardRoute


@Composable
fun HubCard(
    hub: Hub,
    onHubClick: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth().
            padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = { onHubClick(hub.id.toString()) }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
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
                Icon(
                    // Use the 'Groups' or 'Group' icon from the Filled set
                    imageVector = Icons.Filled.Groups,

                    contentDescription = "Group of People",


                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                val quantidade = hub.quantidadeMembros ?: 0
                Text(text = "$quantidade", style=MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun AdministrarScreen(navController: NavHostController) {
    AdaptiveScreenContainer(){
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hubs que administro", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                   // navController.navigate(DashboardRoute.CriarHub)
                },
            ) {
                Text("Criar", style=MaterialTheme.typography.labelMedium)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(hubsList) { hubAtual ->
                HubCard(
                    hub = hubAtual,
                    onHubClick = { hubId ->
                        navController.navigate(
                            DashboardRoute.DetalhesHub(hubId = hubId)
                        )
                    }
                )
            }
        }
    }}
}