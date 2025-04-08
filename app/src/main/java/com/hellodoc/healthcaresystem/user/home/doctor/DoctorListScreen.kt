package com.hellodoc.healthcaresystem.user.home.doctor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.hellodoc.healthcaresystem.R

@Composable
fun DoctorListScreen(sharedPreferences: SharedPreferences, specialtyId: String, onBack: () -> Unit) {
    // Sample list of doctors
    val doctors = listOf(
        Doctor(name = "Thạc sĩ, Bác sĩ Phuong", specialty = "Gan", address = "23/2 đường Quang Trung, phường 12, quận Gò Vấp"),
        Doctor(name = "Thạc sĩ, Bác sĩ Nguyễn Hồng Minh", specialty = "Cơ xương khớp", address = "23/2 đường Quang Trung, phường 12, quận Gò Vấp"),
        Doctor(name = "Thạc sĩ, Bác sĩ Lê Tấn Lợi", specialty = "Ngoại khoa", address = "23/2 đường Quang Trung, phường 12, quận Gò Vấp")
    )

    // Filter doctors by specialtyId
    val filteredDoctors = doctors.filter { it.specialty == specialtyId }
    println(filteredDoctors)

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        TopBar(onClick = onBack)

        Spacer(modifier = Modifier.height(16.dp))

        // List of filtered doctors
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(filteredDoctors) { doctor ->
                DoctorItem(doctor = doctor)
                Spacer(modifier = Modifier.height(8.dp))
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
        Text(
            text = "Danh sách bác sĩ",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DoctorItem(doctor: Doctor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.doctor), // Replace with an actual doctor image resource
            contentDescription = "Doctor image",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = doctor.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = doctor.specialty,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = doctor.address,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Button(
            onClick = { /* Handle button click here */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Đặt lịch khám",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

data class Doctor(
    val name: String,
    val specialty: String,
    val address: String
)
