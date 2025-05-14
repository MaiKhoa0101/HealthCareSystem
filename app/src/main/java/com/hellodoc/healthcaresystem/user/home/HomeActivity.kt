package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
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
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.messaging.FirebaseMessaging
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.doctor.EditClinicServiceScreen
import com.hellodoc.healthcaresystem.doctor.RegisterClinic
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.bmiChecking.BMICheckerScreen
import com.hellodoc.healthcaresystem.user.home.booking.AppointmentDetailScreen
import com.hellodoc.healthcaresystem.user.home.booking.AppointmentListScreen
import com.hellodoc.healthcaresystem.user.home.booking.BookingCalendarScreen
import com.hellodoc.healthcaresystem.user.home.booking.ConfirmBookingScreen
import com.hellodoc.healthcaresystem.user.home.booking.DoctorListActivity
import com.hellodoc.healthcaresystem.user.notification.NotificationPage
import com.hellodoc.healthcaresystem.user.personal.ActivityManagerScreen
import com.hellodoc.healthcaresystem.user.personal.DoctorScreen
import com.hellodoc.healthcaresystem.user.personal.EditUserProfile
import com.hellodoc.healthcaresystem.user.personal.PostListScreen
import com.hellodoc.healthcaresystem.user.personal.PostListScreen2
import com.hellodoc.healthcaresystem.user.personal.ProfileOtherUserPage
import com.hellodoc.healthcaresystem.user.personal.ProfileUserPage
import com.hellodoc.healthcaresystem.user.post.PostDetailScreen
import com.hellodoc.healthcaresystem.user.post.CreatePostScreen
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.RemoteMedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
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
            HealthCareSystemTheme {
                val context = LocalContext.current

                // Initialize ViewModels at activity scope
                val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
                    initializer { UserViewModel(sharedPreferences) }
                })
                val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
                    initializer { DoctorViewModel(sharedPreferences) }
                })
                val specialtyViewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
                    initializer { SpecialtyViewModel(sharedPreferences) }
                })
                val medicalOptionViewModel: MedicalOptionViewModel = viewModel(factory = viewModelFactory {
                    initializer { MedicalOptionViewModel(sharedPreferences) }
                })
                val remoteMedicalOptionViewModel: RemoteMedicalOptionViewModel = viewModel(factory = viewModelFactory {
                    initializer { RemoteMedicalOptionViewModel(sharedPreferences) }
                })
                val geminiViewModel: GeminiViewModel = viewModel(factory = viewModelFactory {
                    initializer { GeminiViewModel(sharedPreferences) }
                })
                val newsViewModel: NewsViewModel = viewModel(factory = viewModelFactory {
                    initializer { NewsViewModel(sharedPreferences) }
                })
                val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
                    initializer { PostViewModel(sharedPreferences) }
                })
                val faqItemViewModel: FAQItemViewModel = viewModel(factory = viewModelFactory {
                    initializer { FAQItemViewModel(sharedPreferences) }
                })

                Index(
                    context = context,
                    navHostController = navHostController,
                    sharedPreferences = sharedPreferences,
                    userViewModel = userViewModel,
                    doctorViewModel = doctorViewModel,
                    specialtyViewModel = specialtyViewModel,
                    medicalOptionViewModel = medicalOptionViewModel,
                    remoteMedicalOptionViewModel = remoteMedicalOptionViewModel,
                    geminiViewModel = geminiViewModel,
                    newsViewModel = newsViewModel,
                    postViewModel = postViewModel,
                    faqItemViewModel = faqItemViewModel
                )
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
        doctorViewModel: DoctorViewModel,
        specialtyViewModel: SpecialtyViewModel,
        medicalOptionViewModel: MedicalOptionViewModel,
        remoteMedicalOptionViewModel: RemoteMedicalOptionViewModel,
        geminiViewModel: GeminiViewModel,
        newsViewModel: NewsViewModel,
        postViewModel: PostViewModel,
        faqItemViewModel: FAQItemViewModel,
        modifier: Modifier = Modifier
    ) {
        GetFcmInstance(sharedPreferences, userViewModel)
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showTopBars = currentRoute in listOf("home")
        val showFootBars = currentRoute in listOf("home", "appointment", "notification", "personal")
        var showFullScreenComment by remember { mutableStateOf(false) } // Local state

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                if (showTopBars && !showFullScreenComment) Headbar(sharedPreferences)
            },
            bottomBar = {
                if (showFootBars && !showFullScreenComment) FootBar(currentRoute,navHostController)
            }
        ) { paddingValues ->
            NavigationHost(
                context = context,
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                userViewModel = userViewModel,
                doctorViewModel = doctorViewModel,
                specialtyViewModel = specialtyViewModel,
                medicalOptionViewModel = medicalOptionViewModel,
                remoteMedicalOptionViewModel = remoteMedicalOptionViewModel,
                geminiViewModel = geminiViewModel,
                newsViewModel = newsViewModel,
                postViewModel = postViewModel,
                faqItemViewModel = faqItemViewModel,
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
        userViewModel: UserViewModel,
        doctorViewModel: DoctorViewModel,
        specialtyViewModel: SpecialtyViewModel,
        medicalOptionViewModel: MedicalOptionViewModel,
        remoteMedicalOptionViewModel: RemoteMedicalOptionViewModel,
        geminiViewModel: GeminiViewModel,
        newsViewModel: NewsViewModel,
        postViewModel: PostViewModel,
        faqItemViewModel: FAQItemViewModel,
        modifier: Modifier = Modifier
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
                    onNavigateToDoctorList = { specialtyId, specialtyName, specialtyDesc ->
                        val intent = Intent(this@HomeActivity, DoctorListActivity::class.java).apply {
                            putExtra("specialtyId", specialtyId)
                            putExtra("specialtyName", specialtyName)
                            putExtra("specialtyDesc", specialtyDesc)
                        }
                        startActivity(intent)
                    },
                    navHostController = navHostController,
                    userViewModel = userViewModel,
                    doctorViewModel = doctorViewModel,
                    specialtyViewModel = specialtyViewModel,
                    medicalOptionViewModel = medicalOptionViewModel,
                    remoteMedicalOptionViewModel = remoteMedicalOptionViewModel,
                    geminiViewModel = geminiViewModel,
                    newsViewModel = newsViewModel,
                    postViewModel = postViewModel,
                    faqItemViewModel = faqItemViewModel
                )
            }
            composable("news_detail") {
                NewsDetailScreen(navHostController,viewModel = newsViewModel)
            }
            composable("appointment") {
                AppointmentListScreen(sharedPreferences, navHostController)
            }
            composable("notification") {
                NotificationPage(context, navHostController)
            }
            composable("personal") {
                ProfileUserPage(sharedPreferences, navHostController)
            }
            composable("otherUserProfile") {
                ProfileOtherUserPage(sharedPreferences, navHostController)
            }
            composable("create_post") {
                CreatePostScreen(context, navHostController)
            }
            composable("editProfile") {
                EditUserProfile(sharedPreferences, navHostController)
            }
            composable("doctorRegister") {
                RegisterClinic(navHostController, sharedPreferences)
            }
            composable("editClinic") {
                EditClinicServiceScreen(sharedPreferences, navHostController)
            }
            composable("gemini_help") {
                GeminiChatScreen(navHostController, sharedPreferences)
            }
            composable("other_user_profile") {
                DoctorScreen(context, navHostController)
            }
            composable("appointment-detail") {
                AppointmentDetailScreen(
                    context = context,
                    onBack = { navHostController.popBackStack() },
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
                        onBack = { navHostController.popBackStack() },
                        navHostController = navHostController
                    )
                }
            }
            composable("booking-confirm") {
                ConfirmBookingScreen(context = context, navHostController = navHostController)
            }
            composable("bmi-checking") {
                BMICheckerScreen(navHostController)
            }
            composable("activity_manager") {
                ActivityManagerScreen(onBack = { navHostController.popBackStack() }, navHostController)
            }
            composable("userComment") {
                PostListScreen(navHostController, sharedPreferences)
            }
            composable("userFavorite") {
                PostListScreen2(navHostController, sharedPreferences)
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
                PostDetailScreen(context, navHostController, postId = postId)
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
                    val userModel = when (userViewModel.getUserAttributeString("role")) {
                        "user" -> "User"
                        "doctor" -> "Doctor"
                        else -> ""
                    }
                    if (userId.isNotEmpty() && userModel.isNotEmpty()) {
                        userViewModel.sendFcmToken(userId, userModel, token)
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomableImageDialog(selectedImageUrl: String?, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    if (selectedImageUrl != null) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable(onClick = onDismiss)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUrl),
                        contentDescription = "Zoomable Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                }
            }
        }
    } else {
        onDismiss()
    }
}