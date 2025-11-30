package com.campusface.screens.validarScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Imports do seu projeto
import com.campusface.components.AdaptiveScreenContainer
import com.campusface.data.Model.Organization
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.data.Repository.OrganizationRepository
import com.campusface.navigation.DashboardRoute

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class ValidarUiState(
    val isLoading: Boolean = false,
    val validatorHubs: List<Organization> = emptyList(), // Lista filtrada (Só onde sou Validador)
    val error: String? = null
)

class ValidarViewModel(
    private val repository: OrganizationRepository = OrganizationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ValidarUiState())
    val uiState = _uiState.asStateFlow()

    private var isLoaded = false

    fun fetchValidatorHubs(token: String?, currentUserId: String?) {
        if (token.isNullOrBlank() || currentUserId.isNullOrBlank()) return

        if (isLoaded) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.getMyHubs(
            token = token,
            onSuccess = { allHubs ->
                isLoaded = true

                // LÓGICA DE FILTRAGEM:
                // Filtra apenas as organizações onde meu ID está na lista de 'validators'
                val onlyValidatorHubs = allHubs.filter { org ->
                    org.validators.any { user -> user.id == currentUserId }
                }

                _uiState.update {
                    it.copy(isLoading = false, validatorHubs = onlyValidatorHubs)
                }
            },
            onError = { errorMsg ->
                _uiState.update {
                    it.copy(isLoading = false, error = errorMsg)
                }
            }
        )
    }

    fun refresh(token: String?, userId: String?) {
        isLoaded = false
        fetchValidatorHubs(token, userId)
    }
}

// ==========================================
// 2. TELA PRINCIPAL (UI)
// ==========================================

@Composable
fun ValidarScreen(
    navController: NavHostController,
    viewModel: ValidarViewModel = viewModel { ValidarViewModel() }
) {
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchValidatorHubs(authState.token, authState.user?.id)
    }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // --- Cabeçalho ---
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Hubs que sou validador", style = MaterialTheme.typography.titleMedium)

                // Em ValidarScreen.kt

                Button(
                    onClick = {
                        // Passa o argumento explicitamente
                        navController.navigate(DashboardRoute.AdicionarMembro(role = "VALIDATOR"))
                    },
                ) {
                    Text("Adicionar", style = MaterialTheme.typography.labelMedium)
                }
            }

            // --- Conteúdo ---
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Erro: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                            IconButton(onClick = { viewModel.refresh(authState.token, authState.user?.id) }) {
                                Icon(Icons.Default.Refresh, "Tentar Novamente")
                            }
                        }
                    }
                }

                uiState.validatorHubs.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Você não é validador em nenhum Hub.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.validatorHubs) { org ->
                            HubValidatorCard(
                                organization = org,
                                onHubClick = {
                                    // Ação do Validador: Geralmente abrir o Scanner de QR Code
                                    // Se você tiver uma rota de Scanner que aceita ID, passe aqui.
                                    navController.navigate(DashboardRoute.QrCodeValidador)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. COMPONENTES (CARD)
// ==========================================

@Composable
fun HubValidatorCard(
    organization: Organization,
    onHubClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onHubClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer, // Cor diferente para destacar Validador
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = organization.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = organization.hubCode,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Ícone de Scanner para indicar a função
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Validar Acesso",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}