package com.campusface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusface.components.CustomTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import com.campusface.components.Logo

@Composable
fun LoginViewModel() {
    var texto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }


        Column(
            modifier = Modifier
                .padding(40.dp, 20.dp)
                .widthIn(max = 325.dp)
                .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        )

        {
            Column(
                modifier = Modifier.padding(0.dp, 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Logo(modifier = Modifier.size(100.dp) )
                Text("Entre Agora", style = MaterialTheme.typography.titleSmall)
                Text("Entre com sua conta no hub", style = MaterialTheme.typography.bodyMedium)
            }

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AlternateEmail,
                        contentDescription = "Email icon"
                    )
                }
            )


            CustomTextField (
                    value = senha,
                    onValueChange = {senha = it},
                    label = "Senha",
                    isPassword = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Lock icon"
                        )
                    }
                )



                Button (
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Entrar")
                }
                OutlinedButton (
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Não tenho uma conta")
                }


        }
    }
