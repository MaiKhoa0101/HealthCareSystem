package com.example.healthcaresystem.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.healthcaresystem.user.home.FootBar
import com.example.healthcaresystem.user.home.Headbar
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme

class AdminRoot: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            HealthCareSystemTheme {
                AdminScreen(
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

@Composable
fun AdminScreen(sharedPreferences: SharedPreferences) {
    Scaffold(
        topBar = { Headbar(sharedPreferences) },

        bottomBar = { FootBar() },
        content = { paddingValues ->
            UserListScreen(
                modifier = Modifier.padding(paddingValues), // Apply paddingValues here
                sharedPreferences = sharedPreferences
            )
        }
    )
}


