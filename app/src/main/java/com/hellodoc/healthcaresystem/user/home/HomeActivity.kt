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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.auth0.android.jwt.JWT
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.doctor.EditClinicServiceScreen
import com.hellodoc.healthcaresystem.doctor.RegisterClinic
import com.hellodoc.healthcaresystem.requestmodel.GetUserID
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.booking.DoctorListActivity
import com.hellodoc.healthcaresystem.user.home.booking.AppointmentListScreen
import com.hellodoc.healthcaresystem.user.home.startscreen.SignIn
import com.hellodoc.healthcaresystem.user.home.startscreen.userLoginExp
import com.hellodoc.healthcaresystem.user.notification.NotificationPage
import com.hellodoc.healthcaresystem.user.personal.EditUserProfile
import com.hellodoc.healthcaresystem.user.personal.ProfileUserPage
import com.hellodoc.healthcaresystem.user.post.PostScreen
import com.hellodoc.healthcaresystem.user.personal.ProfileScreen
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

class HomeActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val navHostController = rememberNavController()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


            //Phải xoá phần này khi deploy//////////////////////////////////////////////////////
            // Dữ liệu đăng nhập mong muốn
            val newEmail = "usertest@gmail.com"
            val newPassword = "123456"

            // Lấy dữ liệu đã lưu (nếu có)
            val savedEmail = sharedPreferences.getString("email", "cu999@gmail.com")
            val savedPassword = sharedPreferences.getString("password", "123456")
            val token = sharedPreferences.getString("access_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2ODA3ZDkxNmIwNDZiMjU2MDJmZmYzZTEiLCJlbWFpbCI6ImN1OTk5QGdtYWlsLmNvbSIsIm5hbWUiOiJjdTY5IiwicGhvbmUiOiIwMTAxMDEwMTAxOSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNzQ1MzQ0ODQ3LCJleHAiOjE3NDU0MzEyNDd9.Q8_wMVyFNki8i2VyPXjL4jiNXvPvsbD04kAM-AZNwOI")

            // Kiểm tra: nếu chưa có token hoặc email/password thay đổi => đăng nhập lại
            if (token == null || savedEmail != newEmail || savedPassword != newPassword) {
                userLoginExp(this, newEmail, newPassword)

                // Lưu lại email, password mới
                with(sharedPreferences.edit()) {
                    putString("email", newEmail)
                    putString("password", newPassword)
                    apply()
                }
            }

            println("SharedPrefs: $sharedPreferences")
            println("Access Token: $token")
            // Phải xoá phần này khi deploy////////////////////////////////////////////////////////////


            // Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
            val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
                initializer { UserViewModel(sharedPreferences) }
            })

            // Lấy dữ liệu user từ StateFlow
            val user by userViewModel.user.collectAsState()

            val jwt = JWT(token.toString())

            val userId = jwt.getClaim("userId").asString()
            println("ID của user lấy đựơc là:"+userId.toString())
            // Gọi API để fetch user từ server
            LaunchedEffect(Unit) {
                userViewModel.getUser(userId.toString())
            }


            HealthCareSystemTheme {
                Index(
                    userId!!,
                    navHostController = navHostController,
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun Index(
        userID: String,
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
                userID = userID,
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun NavigationHost(
        userID: String,
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
                            putExtra("userID", userID)
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
                NotificationPage(navHostController)
            }
            composable("personal") {
                ProfileUserPage(sharedPreferences,navHostController)
            }
            composable("create_post") {
                PostScreen(navHostController)
            }
            composable("editProfile") {
                EditUserProfile(navHostController)
            }
            composable("doctorRegister") {
                RegisterClinic(navHostController)
            }
            composable("editClinic") {
                EditClinicServiceScreen(navHostController)
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
