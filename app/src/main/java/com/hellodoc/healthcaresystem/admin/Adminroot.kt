package com.hellodoc.healthcaresystem.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.responsemodel.SidebarItem
import com.hellodoc.healthcaresystem.user.home.HeadbarPara
import kotlinx.coroutines.launch
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.local.AppointmentViewModelFactory
import com.hellodoc.healthcaresystem.local.dao.AppointmentDao
import com.hellodoc.healthcaresystem.user.home.MyApplication
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel

class AdminRoot : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val dao = (application as MyApplication).database.appointmentDao()
        val viewModelFactory = AppointmentViewModelFactory(sharedPreferences, dao)
        val appointmentViewModel = ViewModelProvider(this, viewModelFactory)[AppointmentViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            HealthCareSystemTheme {
                AdminScreen(sharedPreferences, dao, appointmentViewModel)
            }
        }
    }
}

// Sidebar item list
val sidebarItems = listOf(
    SidebarItem(
        nameField = "Bảng điều khiển",
        iconField = R.drawable.controller,
        navigationField = "Controller"
    ),
    SidebarItem(
        nameField = "Quản lý tài khoản",
        iconField = R.drawable.usermanage,
        navigationField = "UserManager"
    ),
//    SidebarItem(
//        nameField = "Quản lý bác sĩ",
//        iconField = R.drawable.doctormanage,
//        navigationField = "DoctorManager"
//    ),
    SidebarItem(
        nameField = "Quản lý chuyên khoa",
        iconField = R.drawable.messagemanage,
        navigationField = "CreateSpecialty"
    ),
    SidebarItem(
        nameField = "Quản lý khiếu nại",
        iconField = R.drawable.reportmanage,
        navigationField = "ReportManager"
    ),
    SidebarItem(
        nameField = "Quản lý tin tức",
        iconField = R.drawable.edit,
        navigationField = "NewsManager"
    ),
    SidebarItem(
        nameField = "Quản lý bài viết",
        iconField = R.drawable.ic_post,
        navigationField = "PostManager"
    ),
    SidebarItem(
        nameField = "Lịch khám",
        iconField = R.drawable.appointmentmanage,
        navigationField = "AppointmentManager"
    ),

    SidebarItem(
        nameField = "Xác thực tài khoản",
        iconField = R.drawable.clarifymanage,
        navigationField = "ClarifyManager"
    )
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminScreen(sharedPreferences: SharedPreferences, dao: AppointmentDao, appointmentViewModel: AppointmentViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                DrawerContent(
                    sidebarItem = sidebarItems,
                    navController = navController,
                    closedrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                HeadbarPara(
                    sharedPreferences = sharedPreferences,
                    opendrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            content = { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "Controller",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("Controller") {
                        ControllerManagerScreen()
                    }
                    composable("UserManager"){
                        UserListScreen()
                    }
//                    composable("DoctorManager") {
//                        DoctorListScreen(sharedPreferences = sharedPreferences)
//                    }
                    composable("CreateSpecialty") {
                        CreateSpecialtyScreen(sharedPreferences)
                    }
                    composable("ReportManager") {
                        ReportManagerScreen()
                    }
                    composable("NewsManager") {
                        NewsManagerScreen(sharedPreferences = sharedPreferences, navController = navController)
                    }
                    composable("CreateNews") {
                        NewsCreateScreen(sharedPreferences = sharedPreferences, navController = navController)
                    }
                    composable("PostManager"){
                        PostManagerScreen(sharedPreferences = sharedPreferences)
                    }
                    composable("AppointmentManager") {
                        AppointmentManagerScreen(sharedPreferences, dao = dao, appointmentViewModel)
                    }
                    composable("ClarifyManager") {
                        ClarifyManagerScreen(sharedPreferences, navController)
                    }
                    composable("pendingDoctorDetail/{userId}") {backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        PendingDoctorDetailScreen(userId = userId, sharedPreferences = sharedPreferences, navController)

                    }
                }
            }
        )
    }
}

@Composable
fun ZoomableImageDialog(selectedImageUrl: String?, onDismiss: () -> Unit) {
    if (selectedImageUrl != null) {
        Dialog(onDismissRequest = onDismiss) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() }, // Dismiss khi chạm nền
                            onDoubleTap = {
                                scale = 1f
                                offset = Offset.Zero
                            }
                        )
                    },
                contentAlignment = Alignment.Center
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
    }
}
