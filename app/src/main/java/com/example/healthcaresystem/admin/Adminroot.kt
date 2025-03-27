package com.example.healthcaresystem.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthcaresystem.admin.LichKhamScreen
//import com.example.healthcaresystem.Admin.pages.LichKhamScreen
import com.example.healthcaresystem.R
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
            Column(modifier = Modifier.padding(paddingValues)) {
                Spacer(modifier = Modifier.height(10.dp)) // Tạo khoảng cách giữa TopBar và nội dung
                LichKhamScreen(
                    viewModel = viewModel(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    )
}


