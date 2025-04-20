package com.hellodoc.healthcaresystem.user.home.startscreen

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentListScreen(
    sharedPreferences: SharedPreferences
) {
    val appointViewModel: AppointmentViewModel = viewModel(factory = viewModelFactory {
        initializer { AppointmentViewModel(sharedPreferences) }
    })

    val appointments by appointViewModel.appointments.collectAsState()

    LaunchedEffect(Unit) {
        appointViewModel.fetchAppointments()
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Chờ khám", "Khám xong", "Đã huỷ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00F1F8))
    ) {
        Text(
            text = "Danh sách lịch hẹn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            val filteredAppointments = when (selectedTab) {
                0 -> appointments.filter { it.status == "pending" }
                1 -> appointments.filter { it.status == "done" }
                2 -> appointments.filter { it.status == "cancelled" }
                else -> appointments
            }

            LazyColumn {
                items(filteredAppointments) { appointment ->
                    AppointmentCard(appointment)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(appointment: AppointmentResponse) {
    val formattedDate = ZonedDateTime.parse(appointment.date)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    formattedDate,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    appointment.time,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }


            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.doctor),
                    contentDescription = "Doctor Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(appointment.doctor.name, fontWeight = FontWeight.Bold)
                    Text(appointment.doctor.specialty.name)
                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Hospital Location",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = appointment.doctor.hospital,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Huỷ")
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Chỉnh sửa", color = Color.White)
                }
            }
        }
    }
}
