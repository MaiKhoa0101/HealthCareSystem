package com.example.healthcaresystem.user.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healthcaresystem.admin.AdminScreen
import com.example.healthcaresystem.admin.UserListScreen
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//            HealthCareSystemTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Index(modifier = Modifier.padding(innerPadding),sharedPreferences= sharedPreferences )
//                }
//            }

            //tam thoi dung de chay admin
            HealthCareSystemTheme {
                AdminScreen(
                    sharedPreferences = sharedPreferences
                )
            }
            //
        }
    }

    @Composable
    fun Index(modifier: Modifier = Modifier, sharedPreferences:SharedPreferences) {
        Scaffold(
            topBar = { Headbar(sharedPreferences) },

            bottomBar = { FootBar() },
            content = { paddingValues ->
                HealthMateHomeScreen(
                    modifier = Modifier.padding(paddingValues), // Apply paddingValues here
                    sharedPreferences = sharedPreferences
                )
            }
        )
    }
}

