package com.campusface.screens.administrarScreen
// Adapte o pacote conforme a localização do seu arquivo

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Supondo que você tenha estas mocks e data classes (elas devem estar em commonMain)
import com.campusface.data.Hub
import com.campusface.data.hubsList
import com.campusface.data.mockSolicitacoesEntrada
import com.campusface.data.mockSolicitacoesAtualizacao

@Composable
fun HubDetailsWithTabs(
    hubId: String,
    onVoltar: () -> Unit
) {
    // 1. Busca de Dados do Hub
    // Converte o ID String para Int se o ID do Hub for Int
    val hub = remember { hubsList.firstOrNull { it.id.toString() == hubId } }

    if (hub == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hub com ID $hubId não encontrado.", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    // 2. Gerenciamento do Estado da Aba
    val tabs = listOf("Membros", "Solicitação de Entrada", "Solicitação de Atualização")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val selectedContentColor = MaterialTheme.colorScheme.primary
    val unselectedContentColor = Color.Gray
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        // --- Header e Botão de Voltar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para lista")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = hub.nome ?: "Hub sem Nome",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            // 💡 Define o contentColor base para a COR SELECIONADA
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
                            // 🚨 LÓGICA DE COR AQUI:
                            color = if (isSelected) selectedContentColor else unselectedContentColor
                        )
                    }
                )
            }
        }


        // --- Conteúdo da Aba Selecionada ---
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
    }
}

// ----------------------------------------------------
// --- CONTEÚDO DAS ABAS ---
// ----------------------------------------------------

@Composable
fun TabContentMembros(hub: Hub) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            val quantidade = hub.quantidadeMembros ?: 0
            Text(
                text = "Membros Ativos: $quantidade",
                fontWeight = FontWeight.Bold
            )
            Divider(Modifier.padding(vertical = 8.dp))
        }
        // Aqui iriam os itens reais da lista de membros (UsuarioCard)
        // Por exemplo:
        // items(membrosDoHub) { membro -> Text(membro.nome) }
    }
}

@Composable
fun TabContentSolicitacaoEntrada(hub: Hub) {
    // Filtrando mocks pelo ID do Hub (simulação)
    val solicitacoes = mockSolicitacoesEntrada.filter { it.hubId.toString() == hub.id.toString() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (solicitacoes.isEmpty()) {
            item { Text("Nenhuma solicitação de entrada pendente.", modifier = Modifier.padding(16.dp)) }
            return@LazyColumn
        }

        items(solicitacoes) { solicitacao ->
            // Você deve criar o SolicitacaoCard que tem botões Aceitar/Recusar
            Text(text = "Solicitação de: ${solicitacao.solicitante.nome}", modifier = Modifier.padding(16.dp))
            Divider()
        }
    }
}

@Composable
fun TabContentSolicitacaoAtualizacao(hub: Hub) {
    // Filtrando mocks pelo ID do Hub (simulação)
    val atualizacoes = mockSolicitacoesAtualizacao.filter { it.hubId.toString() == hub.id.toString() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (atualizacoes.isEmpty()) {
            item { Text("Nenhuma solicitação de atualização de foto pendente.", modifier = Modifier.padding(16.dp)) }
            return@LazyColumn
        }

        items(atualizacoes) { solicitacao ->
            // Você deve criar o AtualizacaoFotoCard que mostra as duas fotos e botões
            Text(text = "Atualização de Foto para: ${solicitacao.solicitante.nome}", modifier = Modifier.padding(16.dp))
            Divider()
        }
    }
}