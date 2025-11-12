package com.hellodoc.healthcaresystem.view.user.home.root

import android.Manifest
import android.app.Application
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.view.user.home.doctor.EditClinicServiceScreen
import com.hellodoc.healthcaresystem.view.user.home.doctor.RegisterClinic
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.view.user.home.chatAi.GeminiChatScreen
import com.hellodoc.healthcaresystem.view.user.home.news.NewsDetailScreen
import com.hellodoc.healthcaresystem.view.user.home.bmiChecking.BMICheckerScreen
import com.hellodoc.healthcaresystem.view.user.home.booking.AppointmentDetailScreen
import com.hellodoc.healthcaresystem.view.user.home.booking.AppointmentListScreen
import com.hellodoc.healthcaresystem.view.user.home.booking.BookingCalendarScreen
import com.hellodoc.healthcaresystem.view.user.home.booking.ConfirmBookingScreen
import com.hellodoc.healthcaresystem.view.user.home.doctor.DoctorListScreen
import com.hellodoc.healthcaresystem.view.user.notification.NotificationPage
import com.hellodoc.healthcaresystem.view.user.personal.ActivityManagerScreen
import com.hellodoc.healthcaresystem.view.user.home.doctor.DoctorScreen
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.FastTalk
import com.hellodoc.healthcaresystem.view.user.personal.EditUserProfile
import com.hellodoc.healthcaresystem.view.user.personal.CommentHistoryScreen
import com.hellodoc.healthcaresystem.view.user.personal.FavouriteHistoryScreen
import com.hellodoc.healthcaresystem.view.user.personal.ProfileOtherUserPage
import com.hellodoc.healthcaresystem.view.user.personal.ProfileUserPage
import com.hellodoc.healthcaresystem.view.user.personal.Setting
import com.hellodoc.healthcaresystem.view.user.post.PostDetailScreen
import com.hellodoc.healthcaresystem.view.user.post.CreatePostScreen
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel
import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import dagger.hilt.android.HiltAndroidApp


public lateinit var firebaseAnalytics: FirebaseAnalytics
@HiltAndroidApp
class MyApp : Application()

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
            HealthCareSystemTheme(darkTheme=darkTheme) {
                val context = LocalContext.current

                Index(
                    context = context,
                    navHostController = navHostController,
                    dao = dao,
                    onToggleTheme = { darkTheme = !darkTheme },
                    darkTheme = darkTheme
                )
            }
        }
    }

    @Composable
    fun GetFcmInstance(userViewModel: UserViewModel = hiltViewModel()) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "FCM Token: $token")
                    val userId = userViewModel.getUserAttribute("userId", context)
                    val userModel = userViewModel.getUserAttribute("role",context)
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
        modifier: Modifier = Modifier,
        dao: AppointmentDao,
        onToggleTheme: () -> Unit,
        darkTheme: Boolean
    ) {
        GetFcmInstance()
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showTopBars = currentRoute in listOf("home")
        val showFootBars = currentRoute in listOf("home", "appointment", "notification", "personal")
        var showFullScreenComment by remember { mutableStateOf(false) } // Local state

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                if (showTopBars && !showFullScreenComment) Headbar()
            },
            bottomBar = {
                if (showFootBars && !showFullScreenComment) FootBar(currentRoute, navHostController)
            }
        ) { paddingValues ->
            NavigationHost(
                context = context,
                navHostController = navHostController,
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
        modifier: Modifier = Modifier,
        dao: AppointmentDao,
        onToggleTheme: () -> Unit,
        darkTheme: Boolean
    ) {
        val userViewModel: UserViewModel = hiltViewModel()
        val sharedPreferences = context.getSharedPreferences("user_prefs", MODE_PRIVATE)
        val user by userViewModel.user.collectAsState()
        val defaultDestination = intent.getStringExtra("navigate-to") ?: "home"
        NavHost(
            navController = navHostController,
            startDestination = defaultDestination,
            modifier = modifier
        ) {
            composable("fast_talk") {
                FastTalk(navHostController, userViewModel, context)
            }
            composable("home") {
                HealthMateHomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    navHostController = navHostController,
                )
            }
            composable("news_detail") {
                NewsDetailScreen(navHostController)
            }
            composable("appointment") {
                AppointmentListScreen(navHostController, dao = dao)
            }
            composable("notification") {
                NotificationPage(context, navHostController)
            }
            composable("personal") {
                ProfileUserPage(
                    navHostController
                )
            }
            composable(
                "otherUserProfile/{userOwnerID}",
                arguments = listOf(
                    navArgument("userOwnerID")
                    { type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val userOwnerID = backStackEntry.arguments?.getString("userOwnerID") ?: ""
                ProfileOtherUserPage(
                    navHostController,
                    userOwnerID
                )
            }
            composable("create_post") {
                CreatePostScreen(context, navHostController)
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
                GeminiChatScreen(navHostController)
            }
            composable("other_user_profile") {
                DoctorScreen(context, navHostController)
            }
            composable("appointment-detail") {
                AppointmentDetailScreen(
                    context = context,
                    navHostController = navHostController,
                    dao
                )
            }
            composable("doctor_list") {
                DoctorListScreen(
                    context = context,
//                    onBack = {
//                        val intent = Intent(this@DoctorListActivity, HomeActivity::class.java)
//                        startActivity(intent)
//                    },
                    navHostController = navHostController
                )
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
                        navHostController = navHostController,
                        dao
                    )
                }
            }
            composable("booking-confirm") {
                ConfirmBookingScreen(context = context, navHostController = navHostController, dao)
            }
            composable("bmi-checking") {
                BMICheckerScreen(navHostController)
            }
            composable("activity_manager") {
                ActivityManagerScreen(
                    onBack = { navHostController.popBackStack() },
                    navHostController
                )
            }
            composable("userComment") {
                CommentHistoryScreen(navHostController)
            }
            composable("userFavorite") {
                FavouriteHistoryScreen(navHostController)
            }
            composable(
                route = "edit_post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                CreatePostScreen(context, navHostController, postId = postId)
            }
            composable(
                route = "post-detail/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(navHostController, postId)
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
