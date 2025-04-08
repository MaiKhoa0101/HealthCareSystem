package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.doctor.Doctor
import com.hellodoc.healthcaresystem.user.home.doctor.DoctorListScreen

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(sharedPreferences = sharedPreferences)
                    }
                }
            }
        }
    }

//    @Composable
//    fun Index(modifier: Modifier = Modifier, sharedPreferences: SharedPreferences) {
//        val navController = rememberNavController()
//        Scaffold(
//            topBar = { Headbar(sharedPreferences) },
//            bottomBar = { FootBar() },
//        ) { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//            ) {
//                HealthMateHomeScreen(
//                    modifier = Modifier.fillMaxSize(),
//                    sharedPreferences = sharedPreferences,
//                    onNavigateToDoctorList = { specialtyId ->
//                        navController.navigate("doctorList/$specialtyId")
//                    }
//                )
//            }
//        }
//    }

    @Composable
    fun AppNavigation(sharedPreferences: SharedPreferences) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                Scaffold(
                    topBar = { Headbar(sharedPreferences) },
                    bottomBar = { FootBar() },
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        HealthMateHomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            sharedPreferences = sharedPreferences,
                            onNavigateToDoctorList = { specialtyId ->
                                navController.navigate("doctorList/$specialtyId")
                            }
                        )
                    }
                }
            }

            // Sample list of doctors
            val doctors = listOf(
                Doctor(
                    name = "Thạc sĩ, Bác sĩ Lê Tấn Lợi",
                    specialty = "Thần kinh",
                    address = "23/2 đường Quang Trung, phường 12, quận Gò Vấp"
                ),
                Doctor(
                    name = "Thạc sĩ, Bác sĩ Lê Tấn Lợi",
                    specialty = "Nội khoa",
                    address = "23/2 đường Quang Trung, phường 12, quận Gò Vấp"
                ),
                Doctor(
                    name = "Thạc sĩ, Bác sĩ Lê Tấn Lợi",
                    specialty = "Ngoại khoa",
                    address = "23/2 đường Quang Trung, phường 12, quận Gò Vấp"
                )
            )

            // Pass specialtyId correctly
            composable(
                "doctorList/{specialtyId}",
                arguments = listOf(navArgument("specialtyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val specialtyId = backStackEntry.arguments?.getString("specialtyId") ?: ""
                // Filter doctors by the selected specialtyId
                val filteredDoctors = doctors.filter { it.specialty == specialtyId }

                println(specialtyId)
                // Pass filtered doctors and specialtyId to the DoctorListScreen
                DoctorListScreen(
                    sharedPreferences = sharedPreferences,
                    specialtyId = specialtyId,
                    onBack = {
                        navController.popBackStack()
                    }

                )
            }
        }
    }
}
