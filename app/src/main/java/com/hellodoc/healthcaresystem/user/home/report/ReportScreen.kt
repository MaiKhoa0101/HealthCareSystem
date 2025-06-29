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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
){
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory {
        initializer { ReportViewModel(sharedPreferences) }
    })


        var selectedType by remember { mutableStateOf("Bác sĩ") }
        var reportContent by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray)
                    .padding(16.dp)
            ) {
                Text("Báo cáo người dùng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Người báo cáo", fontWeight = FontWeight.Medium)
                Text(youTheCurrentUserUseThisApp!!.name, color = Color.DarkGray)

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
                        color = Color.Red,
                        modifier = Modifier
                            .clickable { onClickShowReportDialog()  }
                            .padding(8.dp),
                        fontWeight = FontWeight.Medium
                    )

                    Button(onClick = {
                        reportViewModel.createReport(context, reportContent, selectedType, youTheCurrentUserUseThisApp!!.id, doctor!!.id, youTheCurrentUserUseThisApp!!.role)
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
    onClickShowPostReportDialog: () ->Unit,
    sharedPreferences: SharedPreferences,

) {
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory {
        initializer { ReportViewModel(sharedPreferences) }
    })


    var selectedType by remember { mutableStateOf("Bác sĩ") }
    var reportContent by remember { mutableStateOf("") }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray)
                    .padding(16.dp)
            ) {
                Text("Báo cáo người dùng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Người báo cáo", fontWeight = FontWeight.Medium)
                Text(youTheCurrentUserUseThisApp!!.name, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Loại báo cáo", fontWeight = FontWeight.Medium)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedType = "Bài viết" }
                    ) {
                        Text("Bài viết", modifier = Modifier.padding(start = 5.dp))
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
                        color = Color.Red,
                        modifier = Modifier
                            .clickable { onClickShowPostReportDialog  }
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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(enabled = true, onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.Gray)
                .padding(16.dp)
        ) {
            Text("Báo cáo người dùng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Người báo cáo", fontWeight = FontWeight.Medium)
            Text(youTheCurrentUserUseThisApp!!.name, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Loại báo cáo", fontWeight = FontWeight.Medium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { selectedType = "Bài viết" }
                ) {
                    Text("Bài viết", modifier = Modifier.padding(start = 5.dp))
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
                    color = Color.Red,
                    modifier = Modifier
                        .clickable { onClickShowPostReportDialog  }
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
