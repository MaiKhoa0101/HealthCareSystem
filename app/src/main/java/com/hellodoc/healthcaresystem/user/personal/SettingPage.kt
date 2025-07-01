package com.example.itforum.user.setting

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Setting(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onToggleTheme: () -> Unit,
    onExitSetting:() -> Unit,
    darkTheme: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .padding(top = 30.dp)
                .padding(horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 30.dp, horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            onExitSetting()
                        }
                )
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 30.dp, horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cài đặt",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        SectionSetting("Chế độ tối", darkTheme, onToggleTheme)

        SectionSetting(
            "Chỉnh sửa thông tin cá nhân",
            iconVector = Icons.Default.Person,
            onPress = {
                navHostController.navigate("editProfile")
            })

        SectionSetting(
            "Đánh giá ứng dụng",
            iconVector = Icons.Default.StarRate,
            onPress = {})

        SectionSetting(
            "Quản lí hoạt động",
            iconVector = Icons.Default.Info,
            onPress = {
                navHostController.navigate("activity_manager")
            })

        SectionSetting(
            nameField = "Đăng xuất",
            iconVector = Icons.Default.Logout,
            onPress = {

            }
        )
    }
}


@Composable
fun SectionSetting(nameField:String,iconVector:ImageVector,onPress:()->Unit, ){
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer
    )
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPress()
            },

        ) {
        Row (
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                nameField,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
            )

            Icon(
                imageVector =iconVector,
                contentDescription = "Toggle Theme",
                modifier = Modifier
                    .size(30.dp)
            )
        }

    }
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer
    )
}
@Composable
fun SectionSetting(nameField:String, darkTheme:Boolean, onPress:()->Unit, ){
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer
    )
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                onPress()
            },
    ) {
        Row (
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                nameField,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
            )

            Icon(
                imageVector =
                    if (darkTheme == false) {
                        Icons.Default.LightMode
                    } else {
                        Icons.Default.DarkMode
                    },
                contentDescription = "Toggle Theme",
                modifier = Modifier
                    .size(30.dp)
            )
        }

    }
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer
    )
}


