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
import androidx.compose.ui.platform.LocalContext
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
        val specialtyDesc = intent.getStringExtra("specialtyDesc") ?: "Chưa có mô tả"
        val userID = intent.getStringExtra("userID") ?: "chua co id ng dung"

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                val context = LocalContext.current
                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val navController = rememberNavController()

                com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Index(
                            modifier = Modifier.padding(innerPadding),
                            context = context,
                            sharedPreferences = sharedPreferences,
                            navHostController = navController,
                            specialtyId = specialtyId,
                            specialtyName = specialtyName,
                            specialtyDesc = specialtyDesc
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
        context: Context,
        sharedPreferences: SharedPreferences,
        navHostController: NavHostController,
        specialtyId: String,
        specialtyName: String,
        specialtyDesc: String
    ) {
        NavHost(
            navController = navHostController,
            startDestination = "doctorList/$specialtyId/$specialtyName/$specialtyDesc"
        ) {
            composable("appointment") {
                AppointmentListScreen(sharedPreferences, navHostController)
            }
            composable("personal") {
                ProfileUserPage(sharedPreferences,navHostController)
            }
            composable(
                route = "doctorList/{specialtyId}/{specialtyName}/{specialtyDesc}",
                arguments = listOf(
                    navArgument("specialtyId") { type = NavType.StringType },
                    navArgument("specialtyName") { type = NavType.StringType },
                    navArgument("specialtyDesc") { type = NavType.StringType}
                )
            ) { backStackEntry ->
                val specialtyId = backStackEntry.arguments?.getString("specialtyId") ?: ""
                val specialtyName =
                    backStackEntry.arguments?.getString("specialtyName") ?: ""
                val specialtyDesc = backStackEntry.arguments?.getString("specialtyDesc") ?: ""

                DoctorListScreen(
                    context = context,
                    specialtyId = specialtyId,
                    specialtyName = specialtyName,
                    specialtyDesc = specialtyDesc,
//                    onBack = {
//                        val intent = Intent(this@DoctorListActivity, HomeActivity::class.java)
//                        startActivity(intent)
//                    },
                    navHostController = navHostController
                )
            }
            composable("booking") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppointmentDetailScreen(
                        context = context,
                        onBack = { navHostController.popBackStack()},
                        navHostController = navHostController
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
                    context = context,
                    navHostController = navHostController
                )
            }

        }
//        }
//    }
    }
}

