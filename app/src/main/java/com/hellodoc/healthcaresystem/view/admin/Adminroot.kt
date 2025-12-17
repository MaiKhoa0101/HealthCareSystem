package com.hellodoc.healthcaresystem.view.admin

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SidebarItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import com.hellodoc.healthcaresystem.model.socket.SocketManager
import javax.inject.Inject
import android.util.Log

@AndroidEntryPoint
class AdminRoot : BaseActivity() {
    @Inject
    lateinit var socketManager: SocketManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)

        if (!token.isNullOrEmpty()) {
            socketManager.connect(token)
            
            // Admin Join Room logic:
            // Admin uses the same socket connection. 
            // The backend's 'joinAdminRoom' event needs to be emitted if we want to receive stats.
            // Or maybe backend uses 'handleConnection' to detect role?
            // Backend code:
            // handleConnection checks token -> sets user online.
            // handleJoinAdminRoom -> client.join('admin').
            
            // We should ideally emit 'joinAdminRoom' if role is Admin.
            // But SocketManager is generic.
            // Let's just connect for now. The backend `handleConnection` sets them online.
            // To receive stats, they might need to emit 'joinAdminRoom'.
            
            // We can add a method in SocketManager to emit events or expose socket.
            socketManager.getSocket()?.emit("joinAdminRoom")
            // But SocketManager.connect acts async, socket might not be connected yet immediately after connect() call?
            // Correct.
            // We should listen for 'connect' event in SocketManager and then emit 'joinAdminRoom' if needed?
            // Or simpler: The backend sets them "Online" just by connecting.
            // "joinAdminRoom" is for receiving 'online_stats'.
            // If the user just wants "online status" tracking, connection is enough.
            // If the user wants Admin Dashboard to show stats, we need 'joinAdminRoom'.
            // The user request was: "to be considered online".
            // So just specific connection is enough for now.
        }

        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                AdminScreen(sharedPreferences)
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
fun AdminScreen(sharedPreferences: SharedPreferences) {
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
                HeadbarAdmin(
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
                        CreateSpecialtyScreen()
                    }
                    composable("ReportManager") {
                        ReportManagerScreen()
                    }
                    composable("NewsManager") {
                        NewsManagerScreen(navController = navController)
                    }
                    composable("CreateNews") {
                        NewsCreateScreen(sharedPreferences = sharedPreferences, navController = navController)
                    }
                    composable("PostManager"){
                        PostManagerScreen()
                    }
                    composable("AppointmentManager") {
                        AppointmentManagerScreen()
                    }
                    composable("ClarifyManager") {
                        ClarifyManagerScreen(navController)
                    }
                    composable("pendingDoctorDetail/{userId}") {backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        PendingDoctorDetailScreen(
                            userId = userId,
                            navController = navController
                        )

                    }
                }
            }
        )
    }
}

