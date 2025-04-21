package com.hellodoc.healthcaresystem.user.home.booking

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
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
import com.hellodoc.healthcaresystem.user.home.HomeActivity
import com.hellodoc.healthcaresystem.user.home.booking.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.doctor.DoctorListScreen
import com.hellodoc.healthcaresystem.user.personal.ProfileUserPage

class DoctorListActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val specialtyId = intent.getStringExtra("specialtyId") ?: "Chưa rõ chuyên khoa"
        val specialtyName = intent.getStringExtra("specialtyName") ?: "Chưa rõ chuyên khoa"
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val navController = rememberNavController()

                com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Index(
                            modifier = Modifier.padding(innerPadding),
                            sharedPreferences = sharedPreferences,
                            navHostController = navController,
                            specialtyId = specialtyId,
                            specialtyName = specialtyName
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun Index(
        modifier: Modifier = Modifier,
        sharedPreferences: SharedPreferences,
        navHostController: NavHostController,
        specialtyId: String,
        specialtyName: String
    ) {
//    Scaffold(
//        topBar = { Headbar(sharedPreferences) },
//        bottomBar = { FootBar(navHostController = navHostController) }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
        NavHost(
            navController = navHostController,
            startDestination = "doctorList/$specialtyId/$specialtyName"
        ) {
//            composable("home") {
//                HealthMateHomeScreen(
//                    modifier = Modifier.fillMaxSize(),
//                    sharedPreferences = sharedPreferences,
//                    onNavigateToDoctorList = { specialtyId, specialtyName ->
//                        // Truyền cả id và name vào route
//                        navHostController.navigate("doctorList/$specialtyId/$specialtyName")
//                    },
//                    onNavigateToDoctorProfile = { doctorId ->
//                        // Truyền cả id và name vào route
//                        navHostController.navigate("doctorList/$specialtyId/$specialtyName")
//                    },
//                    navHostController = navHostController
//                )
//            }
            composable("appointment") {
                AppointmentListScreen(sharedPreferences)
            }
            composable("personal") {
                ProfileUserPage(navHostController)
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
                    onBack = {
                        val intent = Intent(this@DoctorListActivity, HomeActivity::class.java)
                        startActivity(intent)
                    },
                    navHostController = navHostController
                )
            }
            composable("booking") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        AppointmentDetailScreen(
                            onBack = { navHostController.popBackStack()},
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences
                        )
                    }
            }

            composable("booking-calendar") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BookingCalendarScreen(
                        navHostController = navHostController
                    )
                }
            }
            composable("booking-confirm") {
                ConfirmBookingScreen(
                    context = this@DoctorListActivity,
                    navHostController = navHostController
                )
            }
        }
//        }
//    }
    }
}

