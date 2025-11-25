package com.campusface.screens.administrarScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer

// Supondo que você tenha estas mocks e data classes (elas devem estar em commonMain)
import com.campusface.data.Hub
import com.campusface.data.hubsList
import com.campusface.data.mockSolicitacoesEntrada
import com.campusface.data.mockSolicitacoesAtualizacao

@Composable
fun DetalhesHubScreen(
    hubId: String,
    navController: NavHostController
) {
    val hub = remember { hubsList.firstOrNull { it.id.toString() == hubId } }

    if (hub == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hub com ID $hubId não encontrado.", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    val tabs = listOf("Membros", "Solicitação de Entrada", "Solicitação de Atualização")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val selectedContentColor = MaterialTheme.colorScheme.primary
    val unselectedContentColor = Color.Gray
    AdaptiveScreenContainer(){
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para lista")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = hub.nome,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = selectedContentColor
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index

                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            color = if (isSelected) selectedContentColor else unselectedContentColor
                        )
                    }
                )
            }
        }


        Crossfade(
            targetState = selectedTabIndex,
            label = "Tab Content Transition",
            modifier = Modifier.fillMaxSize()
        ) { index ->
            when (index) {
                0 -> TabContentMembros(hub)
                1 -> TabContentSolicitacaoEntrada(hub)
                2 -> TabContentSolicitacaoAtualizacao(hub)
            }
        }
    }}
}

@Composable
fun TabContentMembros(hub: Hub) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            val quantidade = hub.quantidadeMembros ?: 0
            Text(
                text = "Membros Ativos: $quantidade",
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun TabContentSolicitacaoEntrada(hub: Hub) {

    val solicitacoes = mockSolicitacoesEntrada.filter { it.hubId.toString() == hub.id.toString() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (solicitacoes.isEmpty()) {
            item { Text("Nenhuma solicitação de entrada pendente.", modifier = Modifier.padding(16.dp)) }
            return@LazyColumn
        }

        items(solicitacoes) { solicitacao ->
            Text(text = "Solicitação de: ${solicitacao.solicitante.nome}", modifier = Modifier.padding(16.dp))
            HorizontalDivider()
        }
    }
}

@Composable
fun TabContentSolicitacaoAtualizacao(hub: Hub) {
    val atualizacoes = mockSolicitacoesAtualizacao.filter { it.hubId.toString() == hub.id.toString() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (atualizacoes.isEmpty()) {
            item { Text("Nenhuma solicitação de atualização de foto pendente.", modifier = Modifier.padding(16.dp)) }
            return@LazyColumn
        }

        items(atualizacoes) { solicitacao ->
            Text(text = "Atualização de Foto para: ${solicitacao.solicitante.nome}", modifier = Modifier.padding(16.dp))
            HorizontalDivider()
        }
    }
}