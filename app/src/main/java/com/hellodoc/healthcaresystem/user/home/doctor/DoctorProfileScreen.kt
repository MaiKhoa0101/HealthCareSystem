package com.hellodoc.healthcaresystem.user.home.doctor

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse

@Composable
fun DoctorProfileScreen(navHostController: NavHostController) {
    val sharedPreferences = navHostController.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val viewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    var doctorId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(savedStateHandle) {
        doctorId = savedStateHandle?.get<String>("doctorId")
    }

    LaunchedEffect(doctorId) {
        doctorId?.let { viewModel.fetchDoctorById(it) }
    }

    val doctor by viewModel.doctor.collectAsState()

    if (doctor != null) {
        ProfileContent(doctor = doctor!!)
    }
}

@Composable
fun ProfileContent(doctor: GetDoctorResponse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00BCD4))
    ) {
        item {
            HeaderSection(doctor)
            IntroSection(doctor.description)
        }
    }
}

@Composable
fun HeaderSection(doctor: GetDoctorResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = doctor.avatarURL ?: ""),
            contentDescription = "Doctor Image",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = doctor.name ?: "null",
            fontSize = 22.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${doctor.experience?.toString() ?: "null"} năm kinh nghiệm",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}


@Composable
fun IntroSection(description: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Giới thiệu",
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description ?: "null",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DoctorProfileScreenPreview() {
//    val fakeDoctor = GetDoctorResponse(
//        id = "1",
//        role = "doctor",
//        email = "doctor@example.com",
//        name = "Bác sĩ Nguyễn Văn A",
//        phone = "0123456789",
//        password = "",
//        specialty = com.hellodoc.healthcaresystem.responsemodel.Specialty(
//            id = "1",
//            name = "Tim mạch"
//        ),
//        experience = 10,
//        description = "Bác sĩ chuyên về tim mạch, tận tâm với bệnh nhân.",
//        imageUrl = "https://cdn-icons-png.flaticon.com/512/4140/4140048.png" // ảnh demo
//    )
//
//    ProfileContent(doctor = fakeDoctor)
//}