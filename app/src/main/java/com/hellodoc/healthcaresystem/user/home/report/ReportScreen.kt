package com.hellodoc.healthcaresystem.user.home.report

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel


@Composable
fun ReportDoctor(
    context: Context,
    youTheCurrentUserUseThisApp: User?,
    doctor: GetDoctorResponse?,
    onClickShowReportDialog: () -> Unit,
    sharedPreferences: SharedPreferences,
) {
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory {
        initializer { ReportViewModel(sharedPreferences) }
    })

    var selectedType by remember { mutableStateOf("Bác sĩ") }
    var reportContent by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onClickShowReportDialog() }) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
                .padding(16.dp)
        ) {
            if (doctor != null)
                Text("Báo cáo bác sĩ ${doctor.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Người báo cáo", fontWeight = FontWeight.Medium)
            Text(youTheCurrentUserUseThisApp!!.name, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Loại báo cáo", fontWeight = FontWeight.Medium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { selectedType = "Bác sĩ" }
                        .padding(end = 10.dp)
                ) {
                    Text("Bác sĩ", modifier = Modifier.padding(start = 5.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Nội dung báo cáo", fontWeight = FontWeight.Medium)
            TextField(
                value = reportContent,
                onValueChange = { reportContent = it },
                placeholder = { Text("Nhập nội dung...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Huỷ",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onClickShowReportDialog() }
                        .padding(8.dp),
                    fontWeight = FontWeight.Medium
                )

                Button(onClick = {
                    reportViewModel.createReport(
                        context,
                        reportContent,
                        selectedType,
                        youTheCurrentUserUseThisApp.id,
                        doctor!!.id,
                        youTheCurrentUserUseThisApp.role
                    )
                    onClickShowReportDialog()
                }) {
                    Text("Gửi báo cáo")
                }
            }
        }
    }
}

@Composable
fun ReportUser(
    context: Context,
    youTheCurrentUserUseThisApp: User?,
    reportedUser: User?,
    onClickShowReportDialog: () -> Unit,
    sharedPreferences: SharedPreferences,
) {
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory {
        initializer { ReportViewModel(sharedPreferences) }
    })

    var selectedType by remember { mutableStateOf("Người dùng") }
    var reportContent by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onClickShowReportDialog() }) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.Gray)
                .padding(16.dp)
        ) {
            if (reportedUser != null)
                Text("Báo cáo người dùng ${reportedUser.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Người báo cáo", fontWeight = FontWeight.Medium)
            Text(youTheCurrentUserUseThisApp!!.name, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Loại báo cáo", fontWeight = FontWeight.Medium)
            Text("Người dùng", color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Nội dung báo cáo", fontWeight = FontWeight.Medium)
            TextField(
                value = reportContent,
                onValueChange = { reportContent = it },
                placeholder = { Text("Nhập nội dung...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Huỷ",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onClickShowReportDialog() }
                        .padding(8.dp),
                    fontWeight = FontWeight.Medium
                )

                Button(onClick = {
                    reportViewModel.createReport(
                        context,
                        reportContent,
                        selectedType,
                        youTheCurrentUserUseThisApp!!.id,
                        reportedUser!!.id,
                        youTheCurrentUserUseThisApp.role
                    )
                    onClickShowReportDialog()
                }) {
                    Text("Gửi báo cáo")
                }
            }
        }
    }
}

@Composable
fun ReportPostDoctor(
    context: Context,
    youTheCurrentUserUseThisApp: User?,
    userReported: GetDoctorResponse?,
    onClickShowPostReportDialog: () -> Unit,
    sharedPreferences: SharedPreferences,
) {
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory {
        initializer { ReportViewModel(sharedPreferences) }
    })

    var selectedType by remember { mutableStateOf("Bài viết") }
    var reportContent by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onClickShowPostReportDialog() }) {
        // Nội dung hộp báo cáo
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
                .padding(16.dp)
        ) {
            if (userReported != null)
                Text("Báo cáo bài viết của ${userReported.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Người báo cáo", fontWeight = FontWeight.Medium)
            if (youTheCurrentUserUseThisApp != null)
                Text(youTheCurrentUserUseThisApp.name, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Loại báo cáo", fontWeight = FontWeight.Medium)
            Text("Bài viết", color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Nội dung báo cáo", fontWeight = FontWeight.Medium)
            TextField(
                value = reportContent,
                onValueChange = { reportContent = it },
                placeholder = { Text("Nhập nội dung...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Huỷ",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onClickShowPostReportDialog() }
                        .padding(8.dp),
                    fontWeight = FontWeight.Medium
                )

                Button(onClick = {
                    if (youTheCurrentUserUseThisApp != null)
                        reportViewModel.createReport(
                            context,
                            reportContent,
                            selectedType,
                            youTheCurrentUserUseThisApp.id,
                            userReported!!.id,
                            youTheCurrentUserUseThisApp.role
                        )
                    onClickShowPostReportDialog()
                }) {
                    Text("Gửi báo cáo")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPostUser(
    context: Context,
    youTheCurrentUserUseThisApp: User?,
    userReported: PostResponse.User,
    onClickShowPostReportDialog: () ->Unit,
    sharedPreferences: SharedPreferences,
    ) {
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory {
        initializer { ReportViewModel(sharedPreferences) }
    })


    var selectedType by remember { mutableStateOf("Bài viết") }
    var reportContent by remember { mutableStateOf("") }


    Dialog (
        onDismissRequest = { onClickShowPostReportDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text("Báo cáo bài viết của ${userReported.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Người báo cáo", fontWeight = FontWeight.Medium)
            Text(youTheCurrentUserUseThisApp!!.name, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Loại báo cáo", fontWeight = FontWeight.Medium)
            Text("Bài viết", color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Nội dung báo cáo", fontWeight = FontWeight.Medium)
            TextField(
                value = reportContent,
                onValueChange = { reportContent = it },
                placeholder = { Text("Nhập nội dung...", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,        // Màu nền của ô TextField
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,                   // Màu chữ khi đang focus
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,                 // Màu chữ khi không focus
                    focusedIndicatorColor = Color.Transparent, // Ẩn viền khi focus
                    unfocusedIndicatorColor = Color.Transparent // Ẩn viền khi không focus
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Huỷ",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onClickShowPostReportDialog()  }
                        .padding(8.dp),
                    fontWeight = FontWeight.Medium
                )

                Button(onClick = {
                    reportViewModel.createReport(context, reportContent, selectedType, youTheCurrentUserUseThisApp!!.id, userReported!!.id, youTheCurrentUserUseThisApp!!.role)
                    onClickShowPostReportDialog()
                }) {
                    Text("Gửi báo cáo")
                }
            }
        }
    }
}
