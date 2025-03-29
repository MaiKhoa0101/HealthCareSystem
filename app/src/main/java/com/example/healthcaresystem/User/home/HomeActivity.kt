package com.example.healthcaresystem.User.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import com.example.healthcaresystem.R
import com.example.healthcaresystem.User.home.model.HeadbarIcon
import com.example.healthcaresystem.User.home.model.SidebarMenuItem
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Index(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun Index(modifier: Modifier = Modifier) {
        Box(modifier = Modifier.padding(top = 45.dp)) {
            Column(
                Modifier.fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Headbar(
                    icon1 = HeadbarIcon(R.drawable.menu_icon),
                    icon2 = HeadbarIcon(R.drawable.doctor),
                    icon3 = HeadbarIcon(R.drawable.time_icon)
                )
                HealthMateHomeScreen()
            }
            val sidebarMenuItems = listOf(
                SidebarMenuItem(Icons.Default.Home, "Trang chủ"),
                SidebarMenuItem(Icons.Default.Person, "Thông tin"),
                SidebarMenuItem(Icons.Default.Settings, "Cài đặt"),
                SidebarMenuItem(Icons.Default.Info, "Topic"),
                SidebarMenuItem(Icons.Default.Search, "Khám bệnh"),
            )
            SidebarMenu(sidebarMenuItems)
            Menu()
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun GreetingPreview() {
        HealthCareSystemTheme {
            Index()
        }
    }
}

