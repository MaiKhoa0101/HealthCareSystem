package com.parkingSystem.parkingSystem.user.home.root

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import com.parkingSystem.core.common.activity.BaseActivity
import com.parkingSystem.parkingSystem.roomDb.data.dao.AppointmentDao
import com.parkingSystem.parkingSystem.ui.theme.ParkingSystemTheme
import com.parkingSystem.parkingSystem.user.home.booking.AppointmentDetailScreen
import com.parkingSystem.parkingSystem.user.home.booking.AppointmentListScreen
import com.parkingSystem.parkingSystem.user.home.booking.BookingCalendarScreen
import com.parkingSystem.parkingSystem.user.home.booking.ConfirmBookingScreen
import com.parkingSystem.parkingSystem.user.home.parking.ParkingSlot
import com.parkingSystem.parkingSystem.user.notification.NotificationPage
import com.parkingSystem.parkingSystem.user.personal.ActivityManagerScreen
import com.parkingSystem.parkingSystem.user.personal.EditUserProfile
import com.parkingSystem.parkingSystem.user.personal.Setting
import com.parkingSystem.parkingSystem.viewmodel.ParkingViewModel
import com.parkingSystem.parkingSystem.viewmodel.FAQItemViewModel
import com.parkingSystem.parkingSystem.viewmodel.MedicalOptionViewModel
import com.parkingSystem.parkingSystem.viewmodel.NewsViewModel
import com.parkingSystem.parkingSystem.viewmodel.RemoteMedicalOptionViewModel
import com.parkingSystem.parkingSystem.viewmodel.SpecialtyViewModel
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel


public lateinit var firebaseAnalytics: FirebaseAnalytics
class HomeActivity : BaseActivity() {
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Permission", "Notification permission granted")
            } else {
                Log.d("Permission", "Notification permission denied")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        super.onCreate(savedInstanceState)
        checkAndRequestNotificationPermission() //kiem tra quyen thong bao
        val dao = (application as MainApplication).database.appointmentDao()

        enableEdgeToEdge()
        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val navHostController = rememberNavController()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ParkingSystemTheme(darkTheme=darkTheme) {
                val context = LocalContext.current

                // Initialize ViewModels at activity scope
                val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
                    initializer { UserViewModel(sharedPreferences) }
                })
                val parkingViewModel: ParkingViewModel = viewModel(factory = viewModelFactory {
                    initializer { ParkingViewModel(sharedPreferences) }
                })

                Index(
                    context = context,
                    navHostController = navHostController,
                    sharedPreferences = sharedPreferences,
                    userViewModel = userViewModel,
                    parkingViewModel = parkingViewModel,
                    dao = dao,
                    onToggleTheme = { darkTheme = !darkTheme },
                    darkTheme = darkTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @Composable
    fun GetFcmInstance(sharedPreferences: SharedPreferences, userViewModel: UserViewModel) {
        LaunchedEffect(Unit) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "FCM Token: $token")
                    val userId = userViewModel.getUserAttributeString("userId")
                    val userModel = userViewModel.getUserAttributeString("role")
                    if (userId.isNotEmpty() && userModel.isNotEmpty()) {
                        userViewModel.sendFcmToken(userId, userModel, token)
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun Index(
        context: Context,
        navHostController: NavHostController,
        sharedPreferences: SharedPreferences,
        userViewModel: UserViewModel,
        parkingViewModel: ParkingViewModel,
        modifier: Modifier = Modifier,
        dao: AppointmentDao,
        onToggleTheme: () -> Unit,
        darkTheme: Boolean
    ) {
        GetFcmInstance(sharedPreferences, userViewModel)
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showTopBars = currentRoute in listOf("home", "setting")
        val showFootBars = currentRoute in listOf("home", "history", "notification", "setting")
        var showFullScreenComment by remember { mutableStateOf(false) } // Local state

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                if (showTopBars && !showFullScreenComment) Headbar(sharedPreferences)
            },
            bottomBar = {
                if (showFootBars && !showFullScreenComment) FootBar(currentRoute, navHostController)
            }
        ) { paddingValues ->
            NavigationHost(
                context = context,
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                userViewModel = userViewModel,
                parkingViewModel = parkingViewModel,
                modifier = Modifier.padding(paddingValues),
                dao,
                onToggleTheme = onToggleTheme,
                darkTheme = darkTheme
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun NavigationHost(
        context: Context,
        navHostController: NavHostController,
        sharedPreferences: SharedPreferences,
        userViewModel: UserViewModel,
        parkingViewModel: ParkingViewModel,
        modifier: Modifier = Modifier,
        dao: AppointmentDao,
        onToggleTheme: () -> Unit,
        darkTheme: Boolean
    ) {
        val user by userViewModel.user.collectAsState()
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
                    navHostController = navHostController,
                    userViewModel = userViewModel,
                    parkingViewModel = parkingViewModel
                )
            }
            composable("history") {
                AppointmentListScreen(sharedPreferences, navHostController, dao = dao)
            }
            composable("notification") {
                NotificationPage(context, navHostController)
            }
            composable("editProfile") {
                EditUserProfile(sharedPreferences, navHostController)
            }
            composable("appointment-detail") {
                AppointmentDetailScreen(
                    context = context,
                    onBack = { navHostController.popBackStack() },
                    navHostController = navHostController,
                    dao
                )
            }
            composable("slot_list/{parkId}") { backStackEntry ->
                val parkId = backStackEntry.arguments?.getString("parkId") ?: ""
                ParkingSlot(context, navHostController, parkId)
            }

            composable("booking-calendar") {
                Column(modifier = Modifier.fillMaxSize()) {
                    BookingCalendarScreen(context = context, navHostController = navHostController)
                }
            }
            composable("booking") {
                Column(modifier = Modifier.fillMaxSize()) {
                    AppointmentDetailScreen(
                        context = context,
                        onBack = { navHostController.popBackStack() },
                        navHostController = navHostController,
                        dao
                    )
                }
            }
            composable("booking-confirm") {
                ConfirmBookingScreen(context = context, navHostController = navHostController, dao)
            }
            composable("activity_manager") {
                ActivityManagerScreen(
                    onBack = { navHostController.popBackStack() },
                    navHostController
                )
            }
            composable("setting") {
                Setting(
                    navHostController,
                    sharedPreferences,
                    user,
                    onToggleTheme = onToggleTheme,
                    darkTheme = darkTheme
                )
            }
        }

    }

}
