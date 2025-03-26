package com.example.healthcaresystem.User.home

import android.content.SharedPreferences
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
=======
>>>>>>> 44ec552a1b4458273b0f3d4330873ed75307f7ee
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
<<<<<<< HEAD
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthcaresystem.R
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.example.healthcaresystem.viewmodel.UserViewModel
import kotlinx.serialization.Serializable
@Composable
fun Headbar(
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val users by viewModel.users.collectAsState()
    var userName by remember { mutableStateOf("Người dùng") }
    var role by remember { mutableStateOf("Người dùng") }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
        userName = viewModel.getUserNameFromToken()
        role = viewModel.getUserRole()
    }

=======
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthcaresystem.User.home.model.HeadbarIcon

//@Composable
//fun HeadbarScreen() {
//    Column() {
//        Headbar(
//            icon1 = R.drawable.menu_icon,
//            icon2 = R.drawable.doctor,
//            icon3 = R.drawable.time_icon,
//        )
//    }
//}
@Composable
fun Headbar(
    icon1: HeadbarIcon,
    icon2: HeadbarIcon,
    icon3: HeadbarIcon,
) {
>>>>>>> 44ec552a1b4458273b0f3d4330873ed75307f7ee
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(color = Color.Yellow).padding(vertical = 5.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
<<<<<<< HEAD
        // Hình bác sĩ ở giữa
        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = "Doctor Icon",
            modifier = Modifier.size(50.dp)
        )

        // Row for left icon and right user info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
=======
        // Icon bên trái
        Image(
            painter = painterResource(id = icon1.iconRes),
            contentDescription = "Logo Icon",
            modifier = Modifier
                .size(50.dp)
                .padding(start = 10.dp, top = 7.dp)
        )

        // Icon ở giữa
        Image(
            painter = painterResource(id = icon2.iconRes),
            contentDescription = "Center Icon",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center)
        )

        // Icon bên phải với văn bản "Lịch hẹn"
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally
>>>>>>> 44ec552a1b4458273b0f3d4330873ed75307f7ee
        ) {
            // Icon bên trái
            Image(
<<<<<<< HEAD
                painter = painterResource(id = R.drawable.menu_icon),
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .size(50.dp)
=======
                painter = painterResource(id = icon3.iconRes),
                contentDescription = "Time Icon",
                modifier = Modifier.padding(top = 8.dp).size(30.dp)
            )
            Text(
                text = "Lịch hẹn",
                fontSize = 12.sp,
                color = Color.Cyan
>>>>>>> 44ec552a1b4458273b0f3d4330873ed75307f7ee
            )

            // Cột chứa Text và nút logout ở bên phải
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Xin chào \n$userName",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Left,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.logout(context) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
