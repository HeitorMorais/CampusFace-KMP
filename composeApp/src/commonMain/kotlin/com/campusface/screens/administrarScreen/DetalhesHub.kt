package com.campusface.screens.administrarScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.campusface.data.Model.Hub
import com.campusface.screens.CreateHubViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Membro(val id: String, val nome: String, val hubId: String)
data class Solicitacao(
    val id: String,
    val hubId: String,
    val nomeUsuario: String,
    val documento: String,
    val fotoUrl: String,
    val fotoNovaUrl: String? = null // Null se for entrada, Preenchido se for atualização
)

// Placeholder para o seu container customizado
@Composable
fun AdaptiveScreenContainer(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) { content() }
}

// ==========================================
// 2. VIEW MODEL & STATE
// ==========================================

data class HubDetailsUiState(
    val isLoading: Boolean = false,
    val hub: Hub? = null,
    val membros: List<Membro> = emptyList(),
    val entryRequests: List<Solicitacao> = emptyList(),
    val updateRequests: List<Solicitacao> = emptyList(),
    val error: String? = null
)

class HubDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HubDetailsUiState())
    val uiState = _uiState.asStateFlow()

    // TODO: Injete seu Repository aqui no construtor
    // private val repository: HubRepository = HubRepository()

    fun fetchHubDetails(hubId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: Chamar sua API aqui
                // val hub = repository.getHub(hubId)
                // val membros = repository.getMembros(hubId)
                // val solicitacoes = repository.getSolicitacoes(hubId)

                // Simulação de sucesso (Remova isso quando tiver a API)
                println("Buscando dados para o Hub ID: $hubId")

                // _uiState.update { it.copy(isLoading = false, hub = hub, membros = membros...) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun aprovarSolicitacao(id: String) {
        viewModelScope.launch {
            try {
                // TODO: Chamar API de aprovação
                // repository.approveRequest(id)
                println("Aprovando solicitação $id...")

                // Atualiza UI localmente após sucesso
                removerSolicitacaoDaLista(id)
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }

    fun recusarSolicitacao(id: String) {
        viewModelScope.launch {
            try {
                // TODO: Chamar API de recusa
                println("Recusando solicitação $id...")
                removerSolicitacaoDaLista(id)
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }

    fun removerMembro(id: String) {
        viewModelScope.launch {
            try {
                // TODO: Chamar API de exclusão
                println("Removendo membro $id...")

                _uiState.update { state ->
                    state.copy(membros = state.membros.filter { it.id != id })
                }
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }

    private fun removerSolicitacaoDaLista(id: String) {
        _uiState.update { state ->
            state.copy(
                entryRequests = state.entryRequests.filter { it.id != id },
                updateRequests = state.updateRequests.filter { it.id != id }
            )
        }
    }
}

// ==========================================
// 3. TELA PRINCIPAL
// ==========================================

@Composable
fun DetalhesHubScreen(
    hubId: String,
    navController: NavHostController,
    viewModel: HubDetailsViewModel = viewModel { HubDetailsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    // Carrega dados ao iniciar a tela
    LaunchedEffect(hubId) {
        viewModel.fetchHubDetails(hubId)
    }

    // Estado de Loading
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Estado de Erro ou Hub não encontrado (se a API retornar 404)
    if (uiState.error != null) { // Adicione || uiState.hub == null se sua API retornar null
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.error ?: "Hub não encontrado",
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = { navController.popBackStack() }) { Text("Voltar") }
            }
        }
        return
    }

    // Se o hub ainda for nulo (estado inicial antes do load), não mostra nada ou loading
    if (uiState.hub == null) return

    val hub = uiState.hub!!
    val tabs = listOf("Membros", "Solicitação de Entrada", "Solicitação de Atualização")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val selectedContentColor = MaterialTheme.colorScheme.primary
    val unselectedContentColor = Color.Gray

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = hub.nome,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // --- TABS ---
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

            // --- CONTEÚDO DAS TABS ---
            Crossfade(
                targetState = selectedTabIndex,
                label = "Tab Content Transition",
                modifier = Modifier.fillMaxSize()
            ) { index ->
                when (index) {
                    0 -> TabContentMembros(
                        listaMembros = uiState.membros,
                        onDelete = { id -> viewModel.removerMembro(id) }
                    )
                    1 -> TabContentSolicitacaoEntrada(
                        listaSolicitacoes = uiState.entryRequests,
                        onAceitar = { id -> viewModel.aprovarSolicitacao(id) },
                        onRecusar = { id -> viewModel.recusarSolicitacao(id) }
                    )
                    2 -> TabContentSolicitacaoAtualizacao(
                        listaSolicitacoes = uiState.updateRequests,
                        onAceitar = { id -> viewModel.aprovarSolicitacao(id) },
                        onRecusar = { id -> viewModel.recusarSolicitacao(id) }
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. SUB-COMPONENTES
// ==========================================

@Composable
fun TabContentMembros(
    listaMembros: List<Membro>,
    onDelete: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (listaMembros.isEmpty()) {
            item { Text("Nenhum membro cadastrado.", modifier = Modifier.padding(16.dp)) }
        } else {
            items(listaMembros) { membro ->
                MembroCard(
                    nome = membro.nome,
                    onDelete = { onDelete(membro.id) }
                )
            }
        }
    }
}

@Composable
fun MembroCard(nome: String, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nome,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Excluir") },
                        onClick = {
                            onDelete()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun TabContentSolicitacaoEntrada(
    listaSolicitacoes: List<Solicitacao>,
    onAceitar: (String) -> Unit,
    onRecusar: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (listaSolicitacoes.isEmpty()) {
            item { Text("Nenhuma solicitação pendente.", modifier = Modifier.padding(16.dp)) }
        } else {
            items(listaSolicitacoes) { solicitacao ->
                SolicitacaoCard(
                    solicitacao = solicitacao,
                    isUpdate = false,
                    onAceitar = { onAceitar(solicitacao.id) },
                    onRecusar = { onRecusar(solicitacao.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun TabContentSolicitacaoAtualizacao(
    listaSolicitacoes: List<Solicitacao>,
    onAceitar: (String) -> Unit,
    onRecusar: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (listaSolicitacoes.isEmpty()) {
            item { Text("Nenhuma solicitação de atualização pendente.", modifier = Modifier.padding(16.dp)) }
        } else {
            items(listaSolicitacoes) { solicitacao ->
                SolicitacaoCard(
                    solicitacao = solicitacao,
                    isUpdate = true,
                    onAceitar = { onAceitar(solicitacao.id) },
                    onRecusar = { onRecusar(solicitacao.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

// --- Componentes Visuais ---

@Composable
fun PhotoCircle(tamanho: Dp = 70.dp, url: String) {
    AsyncImage(
        modifier = Modifier.size(tamanho).padding(4.dp).clip(CircleShape),
        model = url,
        contentDescription = null,
        placeholder = ColorPainter(Color.LightGray),
        fallback = ColorPainter(Color.LightGray),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun SolicitacaoCard(
    solicitacao: Solicitacao,
    isUpdate: Boolean,
    onAceitar: () -> Unit,
    onRecusar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (isUpdate && solicitacao.fotoNovaUrl != null) {
                    PhotoCircle(tamanho = 50.dp, url = solicitacao.fotoNovaUrl)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Mudar para",
                        modifier = Modifier.size(16.dp).padding(horizontal = 4.dp),
                        tint = Color.Gray
                    )
                }

                PhotoCircle(url = solicitacao.fotoUrl)

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(solicitacao.nomeUsuario, style = MaterialTheme.typography.bodyMedium)
                    Text(solicitacao.documento, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onRecusar,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(end = 8.dp)
                ) { Text("Recusar") }

                OutlinedButton(
                    onClick = onAceitar,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xff009929)),
                ) { Text("Aceitar") }
            }
        }
    }
}