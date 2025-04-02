package com.example.healthcaresystem.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthcaresystem.R
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.example.healthcaresystem.user.home.FootBar
import com.example.healthcaresystem.user.home.HeadbarPara
import com.example.healthcaresystem.user.home.model.SidebarItem
import kotlinx.coroutines.launch

class AdminRoot : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            HealthCareSystemTheme {
                AdminScreen(
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

val sidebarItems = listOf(
    SidebarItem(
        nameField = "Bảng điều khiển",
        iconField = R.drawable.controller,
        navigationField = "Controller"
    ),
    SidebarItem(
        nameField = "Quản lý tài khoản User",
        iconField = R.drawable.usermanage,
        navigationField = "UserManager"
    ),
    SidebarItem(
        nameField = "Quản lý Doctor",
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
        nameField = "Quản lý nhân sự",
        iconField = R.drawable.employeemanage,
        navigationField = "EmployeeManager"
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
                modifier = Modifier
                    .width(300.dp)
            ) {
                DrawerContent(
                    sidebarItem = sidebarItems,
                    navController = navController,
                    closedrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
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
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
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
                    composable("Controller"){
                        ControllerManagerScreen()
                    }
                    composable("UserManager"){
                        UserListScreen(
                            modifier = Modifier.padding(),
                            sharedPreferences = sharedPreferences
                        )
                    }
                    composable("DoctorManager"){
                        DoctorListScreen(sharedPreferences = sharedPreferences)

                    }
                    composable("MessageManager"){
                        MessageManagerScreen()
                    }
                    composable("ReportManager"){
                        ReportManagerScreen()
                    }
                    composable("EmployeeManager"){
                        EmployeeManagerScreen()
                    }
                    composable("AppointmentManager"){
                        LichKhamScreen(
                            viewModel = viewModel(),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                    composable("ClarifyManager"){
                        ClarifyManagerScreen()
                    }
                }
            }
        )
    }
}


