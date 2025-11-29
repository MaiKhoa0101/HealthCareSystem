package com.hellodoc.healthcaresystem.view.user.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun EditOptionPage(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = hiltViewModel()
    val user by userViewModel.you.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getYou(context)
        println("Setting được gọi")
        println("user la: $user")
    }

    val clinicButtonText = if (user?.role == "User") {
        "Đăng kí phòng khám"
    } else {
        "Quản lý phòng khám"
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFD1DC), // Pinkish top
                            Color(0xFFB2F5EA)  // Cyan bottom
                        )
                    )
                )
                .padding(top = 40.dp, bottom = 20.dp)
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
                    tint = Color.Black
                )

                Text(
                    text = "Chỉnh sửa",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(28.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Edit Profile
            EditOptionItem(
                text = "Chỉnh sửa thông tin cá nhân",
                icon = Icons.Default.Person,
                onClick = { navHostController.navigate("editProfile") }
            )

            // Clinic Management
            EditOptionItem(
                text = clinicButtonText,
                icon = Icons.Default.MedicalServices,
                onClick = {
                    if (user == null) return@EditOptionItem
                    if (user!!.role == "User") {
                        navHostController.navigate("doctorRegister")
                    } else {
                        navHostController.navigate("editClinic")
                    }
                }
            )
        }
    }
}

@Composable
fun EditOptionItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF64FCDA)) // Cyan color
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
    }
}
