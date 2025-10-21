package com.parkingSystem.parkingSystem.admin

import android.content.Context
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
import com.parkingSystem.core.common.activity.BaseActivity
import com.parkingSystem.parkingSystem.ui.theme.ParkingSystemTheme
import com.parkingSystem.parkingSystem.responsemodel.SidebarItem
import com.parkingSystem.parkingSystem.roomDb.data.dao.AppointmentDao
import com.parkingSystem.parkingSystem.user.home.root.MainApplication
import kotlinx.coroutines.launch
import com.parkingSystem.parkingSystem.R

class AdminRoot : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val dao = (application as MainApplication).database.appointmentDao()
            ParkingSystemTheme {
                AdminScreen(sharedPreferences, dao = dao)
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
        nameField = "Quản lý bãi đỗ xe",
        iconField = R.drawable.messagemanage,
        navigationField = "CreateSpecialty"
    ),
    SidebarItem(
        nameField = "Quản lý khiếu nại",
        iconField = R.drawable.reportmanage,
        navigationField = "ReportManager"
    ),

)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminScreen(sharedPreferences: SharedPreferences, dao: AppointmentDao) {
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
                    composable("ReportManager") {
                        ReportManagerScreen()
                    }
                    composable("AppointmentManager") {
                        AppointmentManagerScreen(sharedPreferences, dao = dao)
                    }
                    composable("ClarifyManager") {
                        ClarifyManagerScreen(sharedPreferences, navController)
                    }
                }
            }
        )
    }
}

