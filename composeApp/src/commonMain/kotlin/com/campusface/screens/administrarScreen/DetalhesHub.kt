package com.campusface.screens.administrarScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusface.components.AdaptiveScreenContainer

// Supondo que você tenha estas mocks e data classes (elas devem estar em commonMain)
import com.campusface.data.Hub
import com.campusface.data.hubsList
import com.campusface.data.membros
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar para lista")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = hub.nome,
                style = MaterialTheme.typography.titleMedium
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

// tab membros
@Composable
fun MembroCard(
    texto: String,
) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {expanded = true}
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Menu de Opções"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Excluir") },
                    onClick = {
                        println("excluido!")
                        expanded = false
                    }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Opção 2") },
                    onClick = {
                        println("Opção 2 Selecionada!")
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun TabContentMembros(hub: Hub) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (membros.isEmpty()) {
            item { Text("Nenhuma membro cadastrado.", modifier = Modifier.padding(16.dp)) }
            return@LazyColumn
        }
        items (membros) { membro ->
            MembroCard(membro.nome)
        }
    }
}

// Tab Solicitacao Entrada

@Composable
fun PhotoCircle(tamanho: Dp = 90.dp) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .background(color = Color(0xff000000), shape = CircleShape)
            .padding(4.dp)
    )
}

@Composable
fun SolicitacaoEntradaCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            Color.Transparent,
        ),
    ) {
        Row(modifier =  Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.padding(10.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                PhotoCircle()
                Column(modifier = Modifier.fillMaxHeight().padding(0.dp,4.dp)){
                    Text("Nome", style = MaterialTheme.typography.bodyMedium)
                    Text("000.000.000-80", style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ){Text("Recusar")}
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xff009929),
                    ),
                ){Text("Aceitar")}
            }
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
            SolicitacaoEntradaCard()
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