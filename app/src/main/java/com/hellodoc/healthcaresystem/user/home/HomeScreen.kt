package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.SharedPreferences

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.core.common.utils.PhoneCallUtils
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.GetFAQItemResponse
import com.hellodoc.healthcaresystem.responsemodel.GetMedicalOptionResponse
import com.hellodoc.healthcaresystem.responsemodel.GetRemoteMedicalOptionResponse
import com.hellodoc.healthcaresystem.responsemodel.GetSpecialtyResponse

import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.RemoteMedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel

//@Composable
//fun Index(modifier: Modifier =Modifier) {
//    Box(modifier = Modifier.padding(top = 45.dp)) {
//        Column(
//            Modifier.fillMaxSize(),
//            Arrangement.Center,
//            Alignment.CenterHorizontally) {
//            HealthMateHomeScreen()
//        }
//        SidebarMenu()
//        Menu()
//    }
//}

@Composable
fun HealthMateHomeScreen(
    modifier: Modifier = Modifier,
    sharedPreferences: SharedPreferences
) {

    val context = LocalContext.current

    val faqItemViewModel: FAQItemViewModel = viewModel(factory = viewModelFactory {
        initializer { FAQItemViewModel(sharedPreferences) }
    })
    val faqItems by faqItemViewModel.faqItems.collectAsState()

    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })
    val doctors by doctorViewModel.doctors.collectAsState()

    val specialtyViewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })
    val specialties by specialtyViewModel.specialties.collectAsState()

    val medicalOptionViewModel: MedicalOptionViewModel = viewModel(factory = viewModelFactory {
        initializer { MedicalOptionViewModel(sharedPreferences) }
    })
    val medicalOptions by medicalOptionViewModel.medicalOptions.collectAsState()

    val remoteMedicalOptionViewModel: RemoteMedicalOptionViewModel = viewModel(factory = viewModelFactory {
        initializer { RemoteMedicalOptionViewModel(sharedPreferences) }
    })
    val remoteMedicalOptions by remoteMedicalOptionViewModel.remoteMedicalOptions.collectAsState()

    val geminiViewModel: GeminiViewModel = viewModel(factory = viewModelFactory {
        initializer { GeminiViewModel(sharedPreferences) }
    })
    val question by geminiViewModel.question.collectAsState()
    val answer by geminiViewModel.answer.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        doctorViewModel.fetchDoctors()
        specialtyViewModel.fetchSpecialties()
        medicalOptionViewModel.fetchMedicalOptions()
        remoteMedicalOptionViewModel.fetchRemoteMedicalOptions()
        faqItemViewModel.fetchFAQItems()

//        userName = viewModel.getUserNameFromToken()
//        role = viewModel.getUserRole()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .background(Color(0xFF00C5CB))
                        .padding(16.dp)
                ) {
                    AssistantQueryRow(
                        onSubmit = { query ->
                            geminiViewModel.askGemini(query)
                            showDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (faqItems.isEmpty()) {
                        EmptyList("tin mới")
                    } else {
                        FAQItemList(context, faqItems = faqItems)
                    }

                }
            }

            item {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                ) {
                    Text(
                        text = "Thuê hòm liên hệ số:",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                    val phoneNumber = "0902426225"
                    val context = LocalContext.current
                    Text(
                        text = phoneNumber,
                        color = Color.Blue,
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                PhoneCallUtils.startCall(context, phoneNumber)
                            }
                    )
                }

            }

            // Dịch vụ toàn diện
            item {
                SectionHeader(title = "Dịch vụ toàn diện")
                if (medicalOptions.isEmpty()) {
                    EmptyList("dịch vụ hệ thống")
                } else {
                    GridServiceList(medicalOptions) { medicalOption ->
                        showToast(context, "Clicked: ${medicalOption.name}")
                    }
                }
            }

            // Chuyên khoa
            item {
                SectionHeader(title = "Chuyên khoa")

                if (specialties.isEmpty()) {
                    EmptyList("chuyên khoa")
                } else {
                    SpecialtyList(context, specialties = specialties)
                }
            }

            // Bác sĩ nổi bật
            item {
                SectionHeader(title = "Bác sĩ nổi bật")

                if (doctors.isEmpty()) {
                    EmptyList("bác sĩ")
                } else {
                    DoctorList(context, doctors = doctors)
                }
            }

            // Khám từ xa
            item {
                SectionHeader(title = "Khám từ xa")

                if (remoteMedicalOptions.isEmpty()) {
                    EmptyList("dịch vụ khám từ xa")
                } else {
                    RemoteMedicalOptionList(context, remoteMedicalOptions = remoteMedicalOptions)
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showDialog) {
            AssistantAnswerDialog(
                question = question,
                answer = answer,
                onDismiss = { showDialog = false }
            )
        }
    }
}

// Hàm này đảm bảo Toast không vi phạm quy tắc của Compose
fun showToast(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun EmptyList(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không có $name",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AssistantAnswerDialog(
    question: String,
    answer: String,
    onDismiss: () -> Unit
) {
    val displayAnswer = if (answer.isBlank()) "Đang xử lý..." else answer

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Trợ lý AI", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(min = 100.dp, max = 300.dp)
            ) {
                item {
                    Text("Q: $question", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A: $displayAnswer")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}


@Composable
fun AssistantQueryRow(
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Đặt câu hỏi cho Trợ lý AI", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(id = R.drawable.submit_arrow),
            contentDescription = "submit question for AI",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    if (text.isNotBlank()) {
                        onSubmit(text) // Gửi dữ liệu
                        text = "" // Xóa text sau khi gửi
                    }
                }
        )
    }
}

@Composable
fun FAQItemList(context: Context, faqItems: List<GetFAQItemResponse>) {
    faqItems.forEach { faqItem ->
        FAQItem(faqItem) {
            showToast(context, "Clicked: ${faqItem.question}")
        }
    }
}

@Composable
fun FAQItem(
    faq: GetFAQItemResponse,
    onSelectQuestion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = faq.question,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f) // Giữ khoảng cách với icon
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Expand",
                tint = Color.Black,
                modifier = Modifier.clickable { onSelectQuestion() }
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.White, thickness = 1.dp) // Đường kẻ ngang
    }
}


@Composable
fun GridServiceList(items: List<GetMedicalOptionResponse>, onClick: (GetMedicalOptionResponse) -> Unit) {
    Column (modifier = Modifier.padding(horizontal = 16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .clickable { onClick(item) },
                    ) {
                        Row(verticalAlignment =  Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.doctor),
                                contentDescription = item.name,
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = item.name, color = Color.Black)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SpecialtyList(context: Context, specialties: List<GetSpecialtyResponse>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        items(specialties) { specialty ->
            SpecialtyItem(specialty) {
                showToast(context, "Clicked: ${specialty.name}")
            }
        }
    }
}

@Composable
fun SpecialtyItem(specialty: GetSpecialtyResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = specialty.name,
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = specialty.name, color = Color.Black, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DoctorList(context: Context, doctors: List<GetDoctorResponse>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(Color(0xFF73E3E7))
            .fillMaxWidth()
            .height(200.dp)
    ) {
        items(doctors) { doctor ->
            DoctorItem(doctor) {
                showToast(context, "Clicked: ${doctor.name}")
            }
        }
    }
}

@Composable
fun DoctorItem(doctor: GetDoctorResponse, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = doctor.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Cyan, shape = CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = doctor.name, fontSize = 15.sp, color = Color.Black, textAlign = TextAlign.Center)
        Text(text = doctor.specialty, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Composable
fun RemoteMedicalOptionList(context: Context, remoteMedicalOptions: List<GetRemoteMedicalOptionResponse>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        items(remoteMedicalOptions) { remoteMedicalOption ->
            RemoteMedicalOption(remoteMedicalOption) {
                showToast(context, "Clicked: ${remoteMedicalOption.name}")
            }
        }
    }
}

@Composable
fun RemoteMedicalOption(service: GetRemoteMedicalOptionResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .background(Color(0xFFFBE9E7), shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),

        contentAlignment = Alignment.Center // Căn giữa nội dung trong Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = service.name,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = service.name, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)
        }
    }
}

