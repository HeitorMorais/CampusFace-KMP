package com.campusface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import campusface.composeapp.generated.resources.Res
import campusface.composeapp.generated.resources.logo
import coil3.compose.AsyncImage
import com.campusface.data.Repository.LocalAuthRepository
import com.campusface.navigation.AppRoute
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
private fun inputColors(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = if (isError) Color(0xFFEF4444) else Color(0xFFE5E7EB),
    focusedBorderColor = if (isError) Color(0xFFEF4444) else Color.Black,
    cursorColor = Color.Black,
    focusedLabelColor = if (isError) Color.Black else Color.Black,
    focusedTextColor = Color.Black,
    errorBorderColor = Color(0xFFEF4444)
)

@Composable
fun RegisterScreen(navController: NavHostController) {
    val authRepo = LocalAuthRepository.current
    val authState by authRepo.authState.collectAsState()

    val scroll = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var imageBytes by remember { mutableStateOf(ByteArray(0)) }
    var selectedFile by remember { mutableStateOf<PlatformFile?>(null) }

    // Estados de erro
    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var documentError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        title = "Selecione uma Imagem"
    ) { file: PlatformFile? ->
        selectedFile = file
        imageError = false
    }

    LaunchedEffect(authState) {
        if (authState.user != null && authState.error == null && !authState.isLoading) {
            navController.navigate(AppRoute.Login)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(420.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Ícone",
                modifier = Modifier.size(90.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Crie sua conta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Text(
                "Entre com sua conta do hub",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .width(360.dp)
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botão de foto com feedback de erro
                Button(
                    onClick = { launcher.launch() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (imageError) Color.Black else Color.Black
                    )
                ) {
                    Text("📷", fontSize = 26.sp)
                }

                val scope = rememberCoroutineScope()

                selectedFile?.let { file ->
                    LaunchedEffect(file) {
                        imageBytes = file.readBytes()
                    }

                    AsyncImage(
                        model = file,
                        contentDescription = "Imagem selecionada",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    "Foto de perfil",
                    color = if (imageError) Color(0xFFEF4444) else Color.Gray,
                    fontSize = 14.sp
                )

                if (imageError) {
                    Text(
                        "Selecione uma foto de perfil",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Campo Nome
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = false
                    },
                    placeholder = { Text("Nome completo", color = Color(0xFFBDBDBD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = fullNameError,
                    colors = inputColors(fullNameError)
                )
                if (fullNameError) {
                    Text(
                        "Nome completo é obrigatório",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Campo CPF
                OutlinedTextField(
                    value = document,
                    onValueChange = {
                        document = it
                        documentError = false
                    },
                    placeholder = { Text("CPF", color = Color(0xFFBDBDBD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = documentError,
                    colors = inputColors(documentError)
                )
                if (documentError) {
                    Text(
                        "CPF é obrigatório",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Campo E-mail
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    placeholder = { Text("E-mail", color = Color(0xFFBDBDBD)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = emailError,
                    colors = inputColors(emailError)
                )
                if (emailError) {
                    Text(
                        "E-mail válido é obrigatório",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Campo Senha
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    placeholder = { Text("Senha", color = Color(0xFFBDBDBD)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = passwordError,
                    colors = inputColors(passwordError)
                )
                if (passwordError) {
                    Text(
                        "Senha é obrigatória",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Mensagem de erro geral
                if (errorMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEE2E2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            errorMessage,
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Botão Criar Conta
                Button(
                    onClick = {
                        // Validação
                        var hasError = false

                        if (fullName.isBlank()) {
                            fullNameError = true
                            hasError = true
                        }

                        if (document.isBlank()) {
                            documentError = true
                            hasError = true
                        }

                        if (email.isBlank() || !email.contains("@")) {
                            emailError = true
                            hasError = true
                        }

                        if (password.isBlank()) {
                            passwordError = true
                            hasError = true
                        }

                        if (selectedFile == null || imageBytes.isEmpty()) {
                            imageError = true
                            hasError = true
                        }

                        if (hasError) {
                            errorMessage = "Por favor, preencha todos os campos obrigatórios"
                            return@Button
                        }

                        errorMessage = ""

                        authRepo.register(
                            fullName,
                            email,
                            password,
                            document,
                            imageBytes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !authState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        if (authState.isLoading) "Criando..." else "Criar conta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(20.dp))

                TextButton(
                    onClick = { navController.navigate(AppRoute.Login) }
                ) {
                    Text(
                        "Já tenho uma conta",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}