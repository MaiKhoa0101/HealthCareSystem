package com.hellodoc.healthcaresystem.user.home.doctor

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.Doctor
import com.hellodoc.healthcaresystem.user.home.booking.doctorId
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import androidx.compose.material3.*

@Composable
fun DoctorListScreen(
    context: Context,
    specialtyId: String,
    specialtyName: String,
    specialtyDesc: String,
    navHostController: NavHostController
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val viewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })

    LaunchedEffect(specialtyId) {
        viewModel.fetchSpecialtyDoctor(specialtyId)
    }

    val doctors by viewModel.filteredDoctors.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(onClick = {
            navHostController.popBackStack()
        }, viewModel)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, Color(0xFFB2EBF2), RoundedCornerShape(12.dp))
                .background(Color(0xFFE0F7FA), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = specialtyName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = specialtyDesc,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (specialtyDesc.length > 100) { // Ngưỡng để hiển thị nút Xem thêm
                Text(
                    text = if (isExpanded) "Thu gọn" else "Xem thêm",
                    fontSize = 14.sp,
                    color = Color(0xFF0288D1),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (doctors.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không tìm thấy bác sĩ trong chuyên khoa này",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(doctors) { doctor ->
                    DoctorItem(
                        navHostController = navHostController,
                        doctor = doctor,
                        specialtyName = specialtyName,
                        specialtyId = specialtyId,
                        specialtyDesc = specialtyDesc,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onClick: () -> Unit, viewModel: SpecialtyViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4))
            .padding(horizontal = 16.dp, vertical = 8.dp),
//            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back Button",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        activity?.finish()
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Tìm bác sĩ",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.filterDoctorsByLocation(searchQuery)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            placeholder = { Text("Nhập địa chỉ, ví dụ: HCM") },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White, // Thay vì backgroundColor
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}


@Composable
fun DoctorItem(navHostController: NavHostController, doctor: Doctor, specialtyName: String, specialtyId: String, specialtyDesc: String, viewModel: SpecialtyViewModel) {
    val isClinicPaused = doctor.isClinicPaused ?: false

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFB2EBF2),
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Dòng thông tin chính
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!doctor.avatarURL.isNullOrBlank()) {
                AsyncImage(
                    model = doctor.avatarURL,
                    contentDescription = doctor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = doctor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bác sĩ", fontSize = 16.sp, color = Color.Gray)
                    if (isClinicPaused) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(color = Color(0xFFFFCDD2), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Tạm ngưng nhận lịch",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
                Text(doctor.name, fontSize = 26.sp, fontWeight = FontWeight.Medium)
                Text(specialtyName, fontSize = 16.sp, color = Color(0xFF0097A7))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dòng địa chỉ
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color(0xFF00BCD4),
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = doctor.address ?: "Chưa cập nhật địa chỉ",
                fontSize = 13.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dòng nút đặt lịch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    println("Doctor ID is get: "+ doctor.id)
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("doctorId", doctor.id)
                    }
                    navHostController.navigate("other_user_profile")
                  },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Đặt lịch khám",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

