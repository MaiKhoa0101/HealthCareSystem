package com.hellodoc.healthcaresystem.view.user.home.doctor

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Doctor

@Composable
fun DoctorListScreen(
    context: Context,
    navHostController: NavHostController
) {
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    var specialtyId by remember { mutableStateOf("") }
    var specialtyName by remember { mutableStateOf("") }
    var specialtyDesc by remember { mutableStateOf("") }
    val specialtyViewModel: SpecialtyViewModel = hiltViewModel()

    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        savedStateHandle?.get<String>("specialtyId")?.let {
            specialtyId = it
        }
        savedStateHandle?.get<String>("specialtyName")?.let {
            specialtyName = it
        }
        savedStateHandle?.get<String>("specialtyDesc")?.let {
            specialtyDesc = it
        }
        isDataLoaded = true
        //println("SPECIALTY ID" + specialtyId + " " + specialtyName + " " + specialtyDesc)
    }


    LaunchedEffect(specialtyId) {
        specialtyViewModel.fetchSpecialtyDoctor(specialtyId)
    }

    val doctors by specialtyViewModel.filteredDoctors.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    if (isDataLoaded) {
        //println(specialtyId + " " + specialtyName + " " + specialtyDesc)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopBar(onClick = {
                navHostController.popBackStack()
            }, specialtyViewModel)


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                println(specialtyName)
                Text(
                    text = specialtyName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))


                Text(
                    text = specialtyDesc,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (specialtyDesc.length > 100) { // Ngưỡng để hiển thị nút Xem thêm
                    Text(
                        text = if (isExpanded) "Thu gọn" else "Xem thêm",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
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
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                } else {
                     items (doctors) { doctor ->
                        DoctorItem(
                            navHostController = navHostController,
                            doctor = doctor,
                            specialtyName = specialtyName,
                            specialtyId = specialtyId,
                            specialtyDesc = specialtyDesc,
                            viewModel = specialtyViewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onClick: () -> Unit, viewModel: SpecialtyViewModel) {
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Tìm bác sĩ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                placeholder = { 
                    Text(
                        text = "Nhập địa chỉ, ví dụ: HCM",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    ) 
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
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
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
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
                        .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = doctor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bác sĩ", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    if (isClinicPaused) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp))
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
                Text(specialtyName, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
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
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = doctor.address ?: "Chưa cập nhật địa chỉ",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Đặt lịch khám",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

