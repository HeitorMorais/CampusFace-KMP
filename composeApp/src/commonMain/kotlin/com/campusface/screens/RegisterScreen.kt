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
import com.campusface.navigation.AppRoute
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
private fun inputColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedBorderColor = Color.Black,
    cursorColor = Color.Black,
    focusedLabelColor = Color.Black,
    focusedTextColor = Color.Black
)


@Composable
fun RegisterScreen(navController : NavHostController) {

    val scroll = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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

                // 📸 Placeholder da foto (versão grande)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .border(2.dp, Color(0xFFE5E7EB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷", fontSize = 26.sp)
                }

                Spacer(Modifier.height(10.dp))
                Text("Foto de perfil", color = Color.Gray, fontSize = 14.sp)

                Spacer(Modifier.height(32.dp))

                // 👉 Nome
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Nome completo", color = Color(0xFFBDBDBD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = inputColors()
                )

                Spacer(Modifier.height(16.dp))

                // 👉 Documento
                OutlinedTextField(
                    value = document,
                    onValueChange = { document = it },
                    placeholder = { Text("CPF", color = Color(0xFFBDBDBD)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = inputColors()
                )

                Spacer(Modifier.height(16.dp))

                // 👉 Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("E-mail", color = Color(0xFFBDBDBD)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = inputColors()
                )

                Spacer(Modifier.height(16.dp))

                // 👉 Senha
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Senha", color = Color(0xFFBDBDBD)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = inputColors()
                )

                Spacer(Modifier.height(24.dp))

                // 👉 Tipo de acesso
                Text(
                    "Tipo de acesso",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))



                // 👉 Botão Criar conta
                Button(
                    onClick = {
                        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || document.isEmpty()) return@Button

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        if (isLoading) "Criando..." else "Criar conta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(20.dp))

                TextButton(
                    onClick = { navController.navigate(AppRoute.Login)}
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