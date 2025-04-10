package com.hellodoc.healthcaresystem.user.home.doctor

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.Doctor
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@Composable
fun DoctorListScreen(
    sharedPreferences: SharedPreferences,
    specialtyId: String,
    specialtyName: String,
    onBack: () -> Unit,
) {

    val viewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })

    LaunchedEffect(specialtyId) {
        viewModel.fetchSpecialtyDoctor(specialtyId)
    }

    val doctors by viewModel.doctors.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        TopBar(onClick = onBack)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (doctors.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
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
                    DoctorItem(doctor = doctor, specialtyName = specialtyName)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TopBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
                .clickable {
                    onClick()
                }
        )
    }
}

@Composable
fun DoctorItem(doctor: Doctor, specialtyName: String) {
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
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = "Doctor Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Thạc sĩ, Bác sĩ", fontSize = 16.sp, color = Color.Gray)
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
                onClick = { /* TODO */ },
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

