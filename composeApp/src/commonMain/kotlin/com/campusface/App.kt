package com.campusface


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.campusface.view.LoginViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.campusface.theme.NoteMarkTheme
@Composable
@Preview
fun App() {
    NoteMarkTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) { LoginViewModel()}
    }
}