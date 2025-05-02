package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable


import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.doctor.EditClinicServiceScreen
import com.hellodoc.healthcaresystem.doctor.RegisterClinic


import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.booking.DoctorListActivity
import com.hellodoc.healthcaresystem.user.home.booking.AppointmentListScreen


import com.hellodoc.healthcaresystem.user.notification.NotificationPage
import com.hellodoc.healthcaresystem.user.personal.EditUserProfile
import com.hellodoc.healthcaresystem.user.personal.ProfileUserPage
import com.hellodoc.healthcaresystem.user.post.PostScreen
import com.hellodoc.healthcaresystem.user.personal.ProfileScreen


class HomeActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val navHostController = rememberNavController()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            HealthCareSystemTheme {
                val context = LocalContext.current
                Index(
                    context = context,
                    navHostController = navHostController,
                    sharedPreferences = sharedPreferences
                )
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun Index(
        context: Context,
        navHostController: NavHostController,
        modifier: Modifier = Modifier,
        sharedPreferences: SharedPreferences
    ) {
        // Lấy route hiện tại để kiểm tra
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Chỉ hiển thị TopBar & BottomBar với các route cụ thể
        val showTopBars = currentRoute in listOf("home")
        val showFootBars = currentRoute in listOf("home", "appointment", "notification", "personal")


        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                if (showTopBars) Headbar(sharedPreferences)
            },
            bottomBar = {
                if (showFootBars) FootBar(navHostController)
            }
        ) { paddingValues ->
            NavigationHost(
                context = context,
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun NavigationHost(
        context: Context,
        navHostController: NavHostController,
        sharedPreferences: SharedPreferences,
        modifier: Modifier = Modifier,
    ) {
        val defaultDestination = intent.getStringExtra("navigate-to") ?: "home"
        NavHost(
            navController = navHostController,
            startDestination = defaultDestination,
            modifier = modifier
        ) {
            composable("home") {
                HealthMateHomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    sharedPreferences = sharedPreferences,
                    onNavigateToDoctorList = { specialtyId, specialtyName ->
                        val intent = Intent(this@HomeActivity, DoctorListActivity::class.java).apply {
                            putExtra("specialtyId", specialtyId)

                            putExtra("specialtyName", specialtyName)
                        }
                        startActivity(intent)
                    },
                    navHostController = navHostController

                )
            }
            composable("appointment") {
                AppointmentListScreen(sharedPreferences)
            }
            composable("notification") {
                NotificationPage(context, navHostController)
            }
            composable("personal") {
                ProfileUserPage(sharedPreferences,navHostController)
            }
            composable("create_post") {
                PostScreen(context, navHostController)
            }
            composable("editProfile") {
                EditUserProfile(sharedPreferences,navHostController)
            }
            composable("doctorRegister") {
                RegisterClinic(navHostController)
            }
            composable("editClinic") {
                EditClinicServiceScreen(sharedPreferences, navHostController)
            }
            composable("gemini_help") {
                GeminiChatScreen(navHostController, sharedPreferences)
            }
            composable("other_user_profile") {
                ProfileScreen(navHostController)
            }
        }
    }
}