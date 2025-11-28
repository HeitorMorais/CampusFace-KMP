package com.campusface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import campusface.composeapp.generated.resources.Res
import com.campusface.components.AdaptiveScreenContainer
import org.jetbrains.compose.resources.painterResource
import campusface.composeapp.generated.resources.back_icon



@Composable
fun CreateHubScreen(
    onBack: () -> Unit = {},
    onCreateHub: (String, String, String) -> Unit = { _, _, _ -> }
) {

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var codigoHub by remember { mutableStateOf("") }

    AdaptiveScreenContainer {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // -------------------------
            // Header com seta + título
            // -------------------------
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


            HubInput(
                value = nome,
                placeholder = "Nome",
                onValueChange = { nome = it }
            )

            Spacer(Modifier.height(15.dp))

            HubInput(
                value = descricao,
                placeholder = "Descrição",
                onValueChange = { descricao = it }
            )

            Spacer(Modifier.height(15.dp))

            HubInput(
                value = codigoHub,
                placeholder = "Código do Hub",
                onValueChange = { codigoHub = it }
            )

            Spacer(Modifier.height(30.dp))


            Button(
                onClick = { onCreateHub(nome, descricao, codigoHub) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
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
