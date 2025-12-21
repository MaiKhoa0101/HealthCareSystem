package com.hellodoc.healthcaresystem.view.user.personal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

import com.hellodoc.healthcaresystem.model.socket.SocketManager

@Composable
fun Setting(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onToggleTheme: () -> Unit,
    darkTheme: Boolean,
    socketManager: SocketManager // Add this
) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = hiltViewModel()
    val user by userViewModel.you.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getYou(context)
        println("Setting được gọi")
        println("user la: $user")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(top = 40.dp, bottom = 20.dp) // Adjust for status bar
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navHostController.popBackStack() },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "Cài đặt",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.size(28.dp)) // Balance the back icon
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dark Mode
            SettingItem(
                text = "Chế độ tối",
                icon = if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                onClick = onToggleTheme
            )

            // Activity Manager
            SettingItem(
                text = "Quản lý hoạt động",
                icon = Icons.Default.History, // Or Info/Schedule
                onClick = { navHostController.navigate("activity_manager") }
            )

            SettingItem(
                text = "Quản lý khiếu nại",
                icon = Icons.Default.Report,
                onClick = { navHostController.navigate("report_manager") }
            )

            // Logout
            SettingItem(
                text = "Đăng xuất",
                icon = Icons.Default.Logout,
                onClick = { logoutWithGoogle(context, sharedPreferences, socketManager) },
                isDestructive = true
            )
        }
    }
}

// ... SettingItem remains same ...
@Composable
fun SettingItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val backgroundColor = if (isDestructive) 
        MaterialTheme.colorScheme.errorContainer 
    else 
        MaterialTheme.colorScheme.secondaryContainer
    
    val contentColor = if (isDestructive) 
        MaterialTheme.colorScheme.error 
    else 
        MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )

        if (!isDestructive) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun logoutWithGoogle(context: Context, sharedPreferences: SharedPreferences, socketManager: SocketManager) {
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
    
    // Disconnect Socket
    socketManager.disconnect()

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


