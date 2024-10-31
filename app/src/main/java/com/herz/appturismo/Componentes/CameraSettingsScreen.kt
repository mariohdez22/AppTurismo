package com.herz.appturismo.Componentes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController

@Composable
fun CameraSettingsScreen(navController: NavHostController) {
    Text("Pantalla de Controles de Cámara", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}