package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.doctor.DoctorListScreen
import com.hellodoc.healthcaresystem.user.home.startscreen.AppointmentListScreen
import com.hellodoc.healthcaresystem.user.notification.NotificationPage
import com.hellodoc.healthcaresystem.user.personal.ProfileUserPage
import com.hellodoc.healthcaresystem.user.post.PostScreen

class HomeActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val navController = rememberNavController()

            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Index(
                        modifier = Modifier.padding(innerPadding),
                        sharedPreferences = sharedPreferences,
                        navHostController = navController
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun Index(
        modifier: Modifier = Modifier,
        sharedPreferences: SharedPreferences,
        navHostController: NavHostController
    ) {
        Scaffold(
            topBar = { Headbar(sharedPreferences) },
            bottomBar = { FootBar(navHostController = navHostController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(
                    navController = navHostController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HealthMateHomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            sharedPreferences = sharedPreferences,
                            onNavigateToDoctorList = { specialtyId, specialtyName ->
                                // Truyền cả id và name vào route
                                navHostController.navigate("doctorList/$specialtyId/$specialtyName")
                            },
                            navHostController = navHostController
                        )
                    }
                    composable("appointment") {
                        AppointmentListScreen()
                    }
                    composable("notification") {
                        NotificationPage(navHostController)
                    }
                    composable("personal") {
                        ProfileUserPage()
                    }
                    composable("create_post") {
                        PostScreen(navHostController)
                    }
                    composable(
                        route = "doctorList/{specialtyId}/{specialtyName}",
                        arguments = listOf(
                            navArgument("specialtyId") { type = NavType.StringType },
                            navArgument("specialtyName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val specialtyId = backStackEntry.arguments?.getString("specialtyId") ?: ""
                        val specialtyName =
                            backStackEntry.arguments?.getString("specialtyName") ?: ""

                        DoctorListScreen(
                            sharedPreferences = sharedPreferences,
                            specialtyId = specialtyId,
                            specialtyName = specialtyName,
                            onBack = { navHostController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
