package com.hellodoc.healthcaresystem.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.home.HeadbarPara
import com.hellodoc.healthcaresystem.user.home.model.SidebarItem
import kotlinx.coroutines.launch

class AdminRoot : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
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
    SidebarItem(
        nameField = "Quản lý bác sĩ",
        iconField = R.drawable.doctormanage,
        navigationField = "DoctorManager"
    ),
    SidebarItem(
        nameField = "Quản lý tin nhắn",
        iconField = R.drawable.messagemanage,
        navigationField = "MessageManager"
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
                    composable("DoctorManager") {
                        DoctorListScreen(sharedPreferences = sharedPreferences)
                    }
                    composable("MessageManager") {
                        MessageManagerScreen()
                    }
                    composable("ReportManager") {
                        ReportManagerScreen()
                    }
                    composable("NewsManager"){
                        NewsManagerScreen()
                    }
                    composable("AppointmentManager") {
                        AppointmentManagerScreen(sharedPreferences)
                    }
                    composable("ClarifyManager") {
                        ClarifyManagerScreen()
                    }
                }
            }
        )
    }
}
