package com.hellodoc.healthcaresystem.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import com.hellodoc.healthcaresystem.R


@Composable
fun EditClinicServiceScreen(navHostController: NavHostController) {
    BodyEditClinicServiceScreen()

}

@Composable
fun HeadbarEditClinic(navHostController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4))
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "Back button",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(28.dp)
                .clickable { navHostController.navigate("personal") }
        )
        Text(
            text = "Chỉnh sửa phòng khám",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BodyEditClinicServiceScreen() {
    val specialties = listOf("Răng hàm mặt", "Nha khoa", "Chấn thương chỉnh hình")

    var selectedSpecialty by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    var hasHomeVisit by remember { mutableStateOf(false) }
    var feeFrom by remember { mutableStateOf("") }
    var feeTo by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var workplace by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "Chức năng này sẽ cho phép bạn chỉnh sửa thông tin phòng khám, dịch vụ của bạn đến các người dùng khác của hệ thống.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Chuyên khoa của bạn", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))

            Box {
                OutlinedTextField(
                    value = selectedSpecialty,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    readOnly = true,
                    label = { Text("Chưa chọn") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    specialties.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedSpecialty = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Ảnh bìa của dịch vụ", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = "Upload Image"
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = "Clinic Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Địa chỉ phòng khám", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = hasHomeVisit,
                    onCheckedChange = { hasHomeVisit = it }
                )
                Text("Có dịch vụ khám tại nhà")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Phí dịch vụ", fontWeight = FontWeight.SemiBold)
            Row {
                OutlinedTextField(
                    value = feeFrom,
                    onValueChange = { feeFrom = it },
                    label = { Text("Từ") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = feeTo,
                    onValueChange = { feeTo = it },
                    label = { Text("Đến") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Thông tin giới thiệu", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nơi làm việc", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = workplace,
                onValueChange = { workplace = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
