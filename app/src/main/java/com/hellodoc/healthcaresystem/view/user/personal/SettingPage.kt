package com.hellodoc.healthcaresystem.view.user.personal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.discordClick
//import com.hellodoc.healthcaresystem.user.home.root.clearToken
//import com.hellodoc.healthcaresystem.user.home.root.logoutWithGoogle
import com.hellodoc.healthcaresystem.view.user.home.startscreen.StartScreen
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import androidx.compose.runtime.getValue

@Composable
fun Setting(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onToggleTheme: () -> Unit,
    darkTheme: Boolean
) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = hiltViewModel()
    val user by userViewModel.you.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getYou(context)
        println("Setting được gọi")
        println("user la: $user")
    }


    val clinicButtonText =  if (user?.role == "User") {
        "Đăng kí phòng khám"
    } else {
        "Quản lý phòng khám"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
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
                    contentDescription = "backIcon",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            navHostController.popBackStack()
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
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
            clinicButtonText,
            iconVector = Icons.Default.Person,
            onPress = {
                if (user == null) {
                    println("Khong vao dc")
                    return@SectionSetting
                }
                else if (user!!.role=="User"){
                    navHostController.navigate("doctorRegister")
                }
                else{
                    navHostController.navigate("editClinic")
                }
            })

//        SectionSetting(
//            "Đánh giá ứng dụng",
//            iconVector = Icons.Default.StarRate,
//            onPress = {})

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
                logoutWithGoogle(context, sharedPreferences)
            }
        )
    }
}

private fun logoutWithGoogle(context: Context, sharedPreferences: SharedPreferences) {
    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()

    // Configure Google Sign In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Clear saved token
    clearToken(sharedPreferences)

    // Sign out from Firebase Auth
    auth.signOut()

    // Sign out from Google
    googleSignInClient.signOut().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Navigate back to StartScreen
            val intent = Intent(context, StartScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

            // Show success message
            Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
        } else {
            // Handle error
            Toast.makeText(context, "Lỗi khi đăng xuất khỏi Google", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun clearToken(sharedPreferences: SharedPreferences) {
    sharedPreferences.edit()
        .remove("access_token")
        .apply()
}

@Composable
fun SectionSetting(nameField:String, iconVector:ImageVector, onPress:()->Unit, ){
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer
    )
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .discordClick { onPress() }, // áp dụng hiệu ứng
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
            .discordClick { onPress() }, // áp dụng hiệu ứng
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


