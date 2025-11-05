package com.hellodoc.healthcaresystem.view.admin

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
    import com.hellodoc.healthcaresystem.R
    import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

    @Composable
    fun HeadbarAdmin(
        sharedPreferences: SharedPreferences,
        opendrawer: ()-> Unit
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
                .height(100.dp)
                .fillMaxWidth()
                .background(color = Color.Cyan).padding(vertical = 5.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Center Image (com.hellodoc.healthcaresystem.user.home.doctor.com.hellodoc.healthcaresystem.user.home.doctor.Doctor Icon)
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = "com.hellodoc.healthcaresystem.user.home.doctor.com.hellodoc.healthcaresystem.user.home.doctor.Doctor Icon",
                modifier = Modifier
                    .size(50.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Icon (Menu)
                IconButton(
                    onClick = { opendrawer() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu_icon),
                        contentDescription = "Menu Icon",
                        tint = Color.Unspecified // Keeps the original icon color
                    )
                }
                // Right User Info and Logout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // User Greeting
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Xin chào,",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = userName,
                            fontSize = 16.sp,
                            color = Color.Black,
                            maxLines = 1
                        )
                    }

                    // Logout Button
                    IconButton(
                        onClick = { viewModel.logout(context) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
