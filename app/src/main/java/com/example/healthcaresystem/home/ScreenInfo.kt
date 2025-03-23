package com.example.healthcaresystem.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InfoScreen(navController: NavController) {
    ScreenContent("Thông tin", navController)
}

@Composable
fun ScreenContent(title: String, navController: NavController) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại")
            }
        }
    }
}