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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.startscreen.AppointmentListScreen

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val navController = rememberNavController()

            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Index(modifier = Modifier.padding(innerPadding),sharedPreferences= sharedPreferences,navController )
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
    fun Index(modifier: Modifier = Modifier, sharedPreferences:SharedPreferences,navController: NavHostController) {
        Scaffold(
            topBar = { Headbar(sharedPreferences) },
            bottomBar = {  FootBar(navController = navController) },
        ) { paddingValues -> // paddingValues được truyền vào content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HealthMateHomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            sharedPreferences = sharedPreferences,
                            navController
                        )
                    }
                    composable("appointment") {
                        AppointmentListScreen()
                    }
                    composable("notification") {
                        NotificationPage(navController)
                    }
                    composable("personal") {
                        ProfileUserPage()
                    }
                }
            }
        }
    }
}


