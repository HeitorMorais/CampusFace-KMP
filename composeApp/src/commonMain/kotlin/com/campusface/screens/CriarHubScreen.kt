package com.campusface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.painterResource

// Imports do seu projeto
import com.campusface.data.Repository.OrganizationRepository
import com.campusface.components.AdaptiveScreenContainer
import campusface.composeapp.generated.resources.Res
import campusface.composeapp.generated.resources.back_icon
import com.campusface.data.Repository.LocalAuthRepository

// ==========================================
// 1. VIEW MODEL & STATE
// ==========================================

data class CreateHubUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CreateHubViewModel(
    private val repository: OrganizationRepository = OrganizationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateHubUiState())
    val uiState = _uiState.asStateFlow()

    fun createHub(nome: String, descricao: String, codigoHub: String, token: String?) {
        // Validação
        if (nome.isBlank() || descricao.isBlank() || codigoHub.isBlank()) {
            _uiState.update { it.copy(error = "Preencha todos os campos") }
            return
        }

        // Token check
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Erro de autenticação: Token inválido") }
            return
        }

        // Loading
        _uiState.update { it.copy(isLoading = true, error = null) }

        // Chamada API
        repository.createOrganization(
            name = nome,
            description = descricao,
            hubCode = codigoHub,
            token = token,
            onSuccess = {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            },
            onError = { msg ->
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        )
    }

    fun resetState() {
        _uiState.value = CreateHubUiState()
    }
}

// ==========================================
// 2. TELA PRINCIPAL
// ==========================================

@Composable
fun CriarHubScreen(
    navController: NavHostController,
    onBack: () -> Unit = { navController.popBackStack() },
    // CORREÇÃO DO ERRO DE FACTORY: Usando o bloco lambda
    viewModel: CreateHubViewModel = viewModel { CreateHubViewModel() }
) {
    // 1. Pega o token do repositório local
    val authRepository = LocalAuthRepository.current
    val authState by authRepository.authState.collectAsState()

    // 2. Estados dos campos de texto
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var codigoHub by remember { mutableStateOf("") }

    // 3. Estado da UI vindo do ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 4. Estado para o Snackbar (Toast)
    val snackbarHostState = remember { SnackbarHostState() }

    // 5. EFEITO: Sucesso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Hub criado com sucesso!")
            onBack() // Navega de volta
            viewModel.resetState()
        }
    }

    // 6. EFEITO: Erro
    LaunchedEffect(uiState.error) {
        uiState.error?.let { erro ->
            snackbarHostState.showSnackbar(erro)
        }
    }

    AdaptiveScreenContainer {
        // Scaffold necessário para exibir o Snackbar corretamente sobre o conteúdo
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent // Mantém o fundo do seu container
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Padding do Scaffold
                    .padding(30.dp),
                horizontalAlignment = Alignment.Start
            ) {

                // --- Header ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBack() }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.back_icon),
                        contentDescription = "Voltar",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Criar novo hub",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(25.dp))

                // --- Inputs ---
                HubInput(
                    value = nome,
                    placeholder = "Nome da Organização",
                    onValueChange = { nome = it }
                )

                Spacer(Modifier.height(15.dp))

                HubInput(
                    value = descricao,
                    placeholder = "Descrição (ex: Prédio A)",
                    onValueChange = { descricao = it }
                )

                Spacer(Modifier.height(15.dp))

                HubInput(
                    value = codigoHub,
                    placeholder = "Código do Hub (ex: HUB-01)",
                    onValueChange = { codigoHub = it }
                )

                Spacer(Modifier.height(30.dp))

                // --- Botão de Criar ---
                Button(
                    onClick = {
                        // Chama o ViewModel (NÃO navega aqui, a navegação é automática no sucesso)
                        viewModel.createHub(nome, descricao, codigoHub, authState.token)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        disabledContainerColor = Color.Gray
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Criar Hub",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. COMPONENTES AUXILIARES
// ==========================================

@Composable
fun HubInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F1F1)),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(start = 20.dp),
                fontSize = 15.sp
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}