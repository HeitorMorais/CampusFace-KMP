package com.campusface.screens.membroScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.navigation.DashboardRoute // Importa suas rotas de dashboard


@Composable
fun ValidarScreen(navController: NavHostController) {
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
                Text("Hubs que sou validador", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = {
                        navController.navigate(DashboardRoute.AdicionarMembro)
                    },
                    modifier = Modifier
                ) {
                    Text("Adicionar", style=MaterialTheme.typography.labelMedium)
                }
            }

            HubListCard(navController = navController, isValidator = true)
        }
    }
}