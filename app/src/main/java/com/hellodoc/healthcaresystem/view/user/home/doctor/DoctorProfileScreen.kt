package com.hellodoc.healthcaresystem.view.user.home.doctor

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse

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
            .background(MaterialTheme.colorScheme.primaryContainer)
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
                .background(MaterialTheme.colorScheme.background),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = doctor.name ?: "null",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.background
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${doctor.experience?.toString() ?: "null"} năm kinh nghiệm",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.background
        )
    }
}


@Composable
fun IntroSection(description: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Giới thiệu",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description ?: "null",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
