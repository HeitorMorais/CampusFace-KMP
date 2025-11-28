package com.campusface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import campusface.composeapp.generated.resources.Res
import com.campusface.components.AdaptiveScreenContainer
import org.jetbrains.compose.resources.painterResource

import campusface.composeapp.generated.resources.profile_placeholder


@Composable
fun MeuPerfilScreen() {

    var nome by remember { mutableStateOf("Perfil top") }
    var telefone by remember { mutableStateOf("44442222242") }
    var email by remember { mutableStateOf("cr7@email.com") }

    AdaptiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(Res.drawable.profile_placeholder),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )


            }

            Spacer(Modifier.height(10.dp))
            Text(
                "Foto de perfil",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E)
            )

            Spacer(Modifier.height(30.dp))

            PerfilInput(value = nome, onValueChange = { nome = it })
            Spacer(Modifier.height(15.dp))

            PerfilInput(value = telefone, onValueChange = { telefone = it })
            Spacer(Modifier.height(15.dp))

            PerfilInput(value = email, onValueChange = { email = it })

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    "Salvar alterações",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PerfilInput(
    value: String,
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
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}
