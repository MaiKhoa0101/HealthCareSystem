package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Index(modifier = Modifier.padding(innerPadding),sharedPreferences= sharedPreferences )
                }
            }

            //tam thoi dung de chay admin
//            HealthCareSystemTheme {
//                AdminScreen(
//                    sharedPreferences = sharedPreferences
//                )
//            }
            //
        }
    }

    @Composable
    fun Index(modifier: Modifier = Modifier, sharedPreferences:SharedPreferences) {
        Scaffold(
            topBar = { Headbar(sharedPreferences) },
            bottomBar = { FootBar() },
        ) { paddingValues -> // paddingValues được truyền vào content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Áp dụng paddingValues để tránh bị che
            ) {
                HealthMateHomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

