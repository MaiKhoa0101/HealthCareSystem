package com.parkingSystem.parkingSystem.admin

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.ui.theme.MainColor
import com.parkingSystem.parkingSystem.ui.theme.MainTheme
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel
@Composable
fun HeadbarAdmin(
    sharedPreferences: SharedPreferences,
    openDrawer: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val users by viewModel.users.collectAsState()
    var userName by remember { mutableStateOf("Người dùng") }
    var role by remember { mutableStateOf("Người dùng") }

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
        userName = viewModel.getUserNameFromToken()
        role = viewModel.getUserRole()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MainColor)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Logo ở giữa
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center)
        )

        // Hàng menu + thông tin user
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút mở Drawer (bên trái)
            IconButton(
                onClick = { openDrawer() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_icon),
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }

            // Thông tin người dùng (bên phải)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Hello,",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                // Logout
                IconButton(
                    onClick = { viewModel.logout(context) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
