package com.hellodoc.healthcaresystem.user.home.doctor

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.personal.InputEditField
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.requestmodel.ApplyDoctorRequest
import com.hellodoc.healthcaresystem.user.personal.userId
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@Composable
fun RegisterClinic(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {

    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })

    Scaffold(
        topBar = { HeadbarResClinic(navHostController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {
            item {
                ContentRegistrationForm(doctorViewModel, sharedPreferences, navHostController = navHostController)
            }
        }
    }
}

@Composable
fun HeadbarResClinic(navHostController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Cyan)
            .padding(20.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "nút lùi về",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.dp)
                .clickable { navHostController.navigate("personal") }
        )
        Text(
            text = "Đăng kí phòng khám",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContentRegistrationForm(viewModel: DoctorViewModel, sharedPreferences: SharedPreferences, navHostController: NavHostController) {
    val context = LocalContext.current

    val specialtyViewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })
    val specialties by specialtyViewModel.specialties.collectAsState()

    val isLoading = viewModel.loading.value

    var CCCDText by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var clinicAddress by remember { mutableStateOf("") }
    var specialtyId by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val selectedSpecialtyName = specialties.find { it.id == specialtyId }?.name ?: "Chọn chuyên khoa"

    var frontCccdUri by remember { mutableStateOf<Uri?>(null) }
    var backCccdUri by remember { mutableStateOf<Uri?>(null) }
    var faceUri by remember { mutableStateOf<Uri?>(null) }
    var licenseUri by remember { mutableStateOf<Uri?>(null) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val isFormValid = remember(CCCDText, licenseNumber, frontCccdUri, backCccdUri, faceUri, licenseUri) {
        CCCDText.isNotBlank() && licenseNumber.isNotBlank() &&
                frontCccdUri != null && backCccdUri != null && faceUri != null && licenseUri != null
    }

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val viewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })

    val applyMessage by viewModel.applyMessage.collectAsState()

    LaunchedEffect(Unit) {
        specialtyViewModel.fetchSpecialties()
        userId = userViewModel.getUserAttributeString("userId")
    }

    LaunchedEffect(applyMessage) {
        Log.d("DEBUG", "applyMessage changed: $applyMessage")
        if (applyMessage == "success") {
            navHostController.popBackStack()
            viewModel.setApplyMessage("")
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 16.dp)
    ) {
        Text(
            "Chức năng này sẽ cho phép bạn mở và quảng bá phòng khám, dịch vụ của bạn đến các người dùng khác của hệ thống. \n" +
                    "Hãy điền các thông tin dưới đây, hãy đảm bảo hình ảnh bạn gửi rõ ràng và không qua chỉnh sửa"
        )

        InputEditField(
            "Mã số căn cước công dân",
            CCCDText, { CCCDText = it }, "05xxxxxxxxx"
        )

        Column {
            Text(
                "Ảnh chụp căn cước công dân",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageInputField(
                    description = "Ảnh CCCD mặt trước",
                    imageUri = frontCccdUri,
                    onImageSelected = { frontCccdUri = it }
                )
                ImageInputField(
                    description = "Ảnh CCCD mặt sau",
                    imageUri = backCccdUri,
                    onImageSelected = { backCccdUri = it }
                )
            }
        }

        Column {
            Text(
                "Ảnh chụp mặt bạn",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageInputField(
                    description = "Ảnh mặt bạn",
                    imageUri = faceUri,
                    onImageSelected = { faceUri = it }
                )
            }
        }

        Column {
            Text(
                "Ảnh đại diện",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageInputField(
                    description = "Ảnh đại diện",
                    imageUri = avatarUri,
                    onImageSelected = { avatarUri = it }
                )
            }
        }

        Column {
            Text("Chuyên khoa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .border(1.dp, Color.Gray)
                    .padding(12.dp)
            ) {
                Text(text = selectedSpecialtyName)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                specialties.forEach { specialty ->
                    DropdownMenuItem(
                        text = { Text(specialty.name) },
                        onClick = {
                            specialtyId = specialty.id
                            expanded = false
                        }
                    )
                }
            }
        }

        InputEditField(
            "Mã giấy phép hành nghề do bộ y tế cấp",
            licenseNumber,
            { licenseNumber = it },
            "Nhập mã giấy phép hành nghề"
        )

        InputEditField(
            "Địa chỉ phòng khám",
            clinicAddress,
            { clinicAddress = it },
            "Nhập địa chỉ phòng khám"
        )

        Column {
            Text(
                "Ảnh giấy phép hành nghề",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageInputField(
                    description = "Ảnh giấy phép hành nghề",
                    imageUri = licenseUri,
                    onImageSelected = { licenseUri = it }
                )
            }
        }

        Text(
            "Tôi cam kết những thông tin tôi đăng là đúng và sẵn sàng chịu trách nhiệm trước pháp luật\n" +
                    "\n" +
                    "Việc bạn thực hiện đăng kí phòng khám trên ứng dụng đồng nghĩa với việc bạn phải tuân thủ theo chính sách về bác sĩ sử dụng dịch vụ trên hệ thống, chi tiết xem tại đây.\n"
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Button(
                onClick = {
                    val request = ApplyDoctorRequest(
                        license = licenseNumber,
                        CCCD = CCCDText,
                        specialty = specialtyId,
                        address = clinicAddress,
                        licenseUrl = licenseUri,
                        faceUrl = faceUri,
                        avatarURL = avatarUri,
                        frontCccdUrl = frontCccdUri,
                        backCccdUrl = backCccdUri
                    )

                    viewModel.applyForDoctor(userId, request, context)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Yêu cầu xét duyệt hồ sơ")
            }
        }
    }
}

@Composable
fun ImageInputField(
    description: String,
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable {
                    launcher.launch("image/*")
                }
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.blankfield),
                            contentDescription = description,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = "Select image",
                            tint = Color.White,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .padding(6.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
            Text(description)
        }
    }
}