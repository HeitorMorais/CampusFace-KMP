package com.campusface.screens.administrarScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- IMPORTS DO SEU PROJETO ---
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.data.Repository.OrganizationMemberRepository
import com.campusface.data.Repository.OrganizationMember
import com.campusface.data.Repository.EntryRequestRepository
import com.campusface.data.Repository.EntryRequest
import com.campusface.data.Repository.OrganizationRepository
import com.campusface.data.Model.Organization

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class HubDetailsUiState(
    val isLoading: Boolean = false,
    val hub: Organization? = null,
    val membros: List<OrganizationMember> = emptyList(),
    val entryRequests: List<EntryRequest> = emptyList(), // Lista de solicitações
    val error: String? = null
)

class HubDetailsViewModel(
    private val memberRepo: OrganizationMemberRepository = OrganizationMemberRepository(),
    private val entryRepo: EntryRequestRepository = EntryRequestRepository(),
    private val orgRepo: OrganizationRepository = OrganizationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HubDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private var currentHubCode: String? = null

    fun fetchHubDetails(hubId: String, token: String?) {
        if (token.isNullOrBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        // 1. Busca Membros
        memberRepo.listMembers(
            organizationId = hubId,
            token = token,
            onSuccess = { listaMembros ->
                _uiState.update { it.copy(membros = listaMembros) }

                // 2. Busca Detalhes do Hub (para pegar o HubCode)
                orgRepo.getMyHubs(
                    token = token,
                    onSuccess = { hubs ->
                        val hubEncontrado = hubs.find { it.id == hubId }
                        if (hubEncontrado != null) {
                            currentHubCode = hubEncontrado.hubCode
                            _uiState.update { it.copy(hub = hubEncontrado) }

                            // 3. Com o HubCode, busca as solicitações de entrada
                            fetchEntryRequests(hubEncontrado.hubCode, token)
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Hub não encontrado") }
                        }
                    },
                    onError = { erro -> _uiState.update { it.copy(isLoading = false, error = erro) } }
                )
            },
            onError = { erro -> _uiState.update { it.copy(isLoading = false, error = erro) } }
        )
    }

    private fun fetchEntryRequests(hubCode: String, token: String) {
        entryRepo.listPendingRequestsByHub(
            hubCode = hubCode,
            token = token,
            onSuccess = { requests ->
                _uiState.update {
                    it.copy(isLoading = false, entryRequests = requests)
                }
            },
            onError = {
                // Se der erro só aqui, paramos o loading mas mantemos o resto
                _uiState.update { it.copy(isLoading = false) }
            }
        )
    }

    fun aprovarSolicitacao(requestId: String, token: String) {
        if (token.isEmpty()) return

        // Remove visualmente (otimista)
        val backup = _uiState.value.entryRequests
        _uiState.update { it.copy(entryRequests = it.entryRequests.filter { r -> r.id != requestId }) }

        entryRepo.approveRequest(
            requestId = requestId,
            token = token,
            onSuccess = {
                // Opcional: Recarregar membros pois entrou gente nova
                // fetchHubDetails(...)
            },
            onError = { msg ->
                // Rollback
                _uiState.update { it.copy(entryRequests = backup, error = "Erro: $msg") }
            }
        )
    }

    fun recusarSolicitacao(requestId: String, token: String) {
        if (token.isEmpty()) return

        val backup = _uiState.value.entryRequests
        _uiState.update { it.copy(entryRequests = it.entryRequests.filter { r -> r.id != requestId }) }

        entryRepo.rejectRequest(
            requestId = requestId,
            token = token,
            onSuccess = {},
            onError = { msg ->
                _uiState.update { it.copy(entryRequests = backup, error = "Erro: $msg") }
            }
        )
    }
}

// ==========================================
// 2. TELA PRINCIPAL (UI)
// ==========================================

@Composable
fun DetalhesHubScreen(
    hubId: String,
    navController: NavHostController,
    viewModel: HubDetailsViewModel = viewModel { HubDetailsViewModel() }
) {
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(hubId) {
        viewModel.fetchHubDetails(hubId, authState.token)
    }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                    text = uiState.hub?.name ?: "Carregando...",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // --- LOADING / ERRO ---
            if (uiState.isLoading && uiState.hub == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null && uiState.hub == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                // --- CONTEÚDO DAS TABS ---
                HubTabsContent(
                    membros = uiState.membros,
                    solicitacoes = uiState.entryRequests,
                    onAprovar = { id -> viewModel.aprovarSolicitacao(id, authState.token ?: "") },
                    onRecusar = { id -> viewModel.recusarSolicitacao(id, authState.token ?: "") }
                )
            }
        }
    }
}

@Composable
fun HubTabsContent(
    membros: List<OrganizationMember>,
    solicitacoes: List<EntryRequest>,
    onAprovar: (String) -> Unit,
    onRecusar: (String) -> Unit
) {
    val tabs = listOf("Membros", "Solicitações", "Atualizações")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        // Mostra contador na tab de solicitações se houver pendências
                        if (index == 1 && solicitacoes.isNotEmpty()) {
                            Text("$title (${solicitacoes.size})")
                        } else {
                            Text(title)
                        }
                    }
                )
            }
        }

        Crossfade(targetState = selectedTabIndex, label = "Tabs") { index ->
            when (index) {
                0 -> TabContentMembros(membros)
                1 -> TabContentSolicitacaoEntrada(solicitacoes, onAprovar, onRecusar)
                2 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sem atualizações pendentes", color = Color.Gray)
                }
            }
        }
    }
}

// ==========================================
// 3. SUB-COMPONENTS (TABS)
// ==========================================

// --- TAB MEMBROS ---
@Composable
fun TabContentMembros(listaMembros: List<OrganizationMember>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (listaMembros.isEmpty()) {
            item {
                Text("Nenhum membro encontrado.", modifier = Modifier.padding(16.dp))
            }
        } else {
            items(listaMembros) { membro ->
                MembroCard(
                    nome = membro.user.fullName,
                    role = membro.role
                )
            }
        }
    }
}

@Composable
fun MembroCard(nome: String, role: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = nome, style = MaterialTheme.typography.bodyMedium)
                Text(text = role, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            IconButton(onClick = { /* Menu */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
            }
        }
    }
}

// --- TAB SOLICITAÇÕES ---
@Composable
fun TabContentSolicitacaoEntrada(
    listaSolicitacoes: List<EntryRequest>,
    onAceitar: (String) -> Unit,
    onRecusar: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (listaSolicitacoes.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhuma solicitação pendente.", color = Color.Gray)
                }
            }
        } else {
            items(listaSolicitacoes) { solicitacao ->
                SolicitacaoCard(
                    solicitacao = solicitacao,
                    onAceitar = { onAceitar(solicitacao.id) },
                    onRecusar = { onRecusar(solicitacao.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SolicitacaoCard(
    solicitacao: EntryRequest,
    onAceitar: () -> Unit,
    onRecusar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Tenta pegar a URL do Cloudinary ou placeholder
                val imageUrl = solicitacao.user.faceImageId ?: ""

                PhotoCircle(url = imageUrl) // Usa o helper

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = solicitacao.user.fullName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cargo: ${solicitacao.role}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    solicitacao.user.document?.let {
                        Text(text = it, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
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

                Button(
                    onClick = onAceitar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A12B)),
                ) { Text("Aceitar") }
            }
        }
    }
}

@Composable
fun PhotoCircle(tamanho: Dp = 60.dp, url: String) {
    AsyncImage(
        modifier = Modifier.size(tamanho).padding(4.dp).clip(CircleShape).background(Color.LightGray),
        model = url,
        contentDescription = null,
        placeholder = ColorPainter(Color.LightGray),
        error = ColorPainter(Color.Gray),
        contentScale = ContentScale.Crop
    )
}