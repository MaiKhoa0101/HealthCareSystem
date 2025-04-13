package com.hellodoc.healthcaresystem.user.personal

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.personal.model.Rating
import com.hellodoc.healthcaresystem.user.personal.model.Review
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.personal.model.AppointmentDetail
import com.hellodoc.healthcaresystem.user.personal.model.AppointmentTime
import com.hellodoc.healthcaresystem.user.personal.model.DoctorInfo
import com.hellodoc.healthcaresystem.user.personal.model.MethodOption
import com.hellodoc.healthcaresystem.user.personal.model.PatientInfo
import com.hellodoc.healthcaresystem.user.personal.model.PriceInfo
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.WriteReviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    detail: AppointmentDetail,
    onBackClick: () -> Unit = {},
    onBookClick: () -> Unit = {}
) {
    val methodOptions = remember { mutableStateListOf<MethodOption>().apply {
        addAll(detail.methodOptions)
    } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. AppBar
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Cyan)
                    .padding(16.dp)
            ) {
                val (backRef, titleRef) = createRefs()

                IconButton(onClick = onBackClick, modifier = Modifier.constrainAs(backRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top, margin = 20.dp)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }

                Text(
                    "Chi tiết lịch hẹn khám",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(backRef.top)
                        bottom.linkTo(backRef.bottom)
                        start.linkTo(backRef.end)
                        end.linkTo(parent.end)
                        centerHorizontallyTo(parent)
                    }
                )
            }

            // 2. Doctor Info
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(detail.doctor.avatar),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Bác sĩ", fontWeight = FontWeight.SemiBold)
                        Text(detail.doctor.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(detail.doctor.specialization, color = Color.Gray)
                        Text(detail.doctor.pricePerHour, color = Color.Black)
                    }
                }
            }

            // 3. Patient Info
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Đặt lịch khám này cho:", fontWeight = FontWeight.Bold)
                        Text("Sửa hồ sơ", color = Color.Blue, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Họ và tên: ${detail.patient.fullName}")
                    Text("Giới tính: ${detail.patient.gender}")
                    Text("Ngày sinh: ${detail.patient.dateOfBirth}")
                    Text("Điện thoại: ${detail.patient.phoneNumber}")
                    Spacer(Modifier.height(8.dp))
                    Text("Xem chi tiết", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Method options
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Phương thức khám", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                methodOptions.forEachIndexed { index, item ->
                    MethodItem(
                        title = item.title,
                        address = item.address,
                        selected = item.isSelected,
                        onClick = {
                            methodOptions.replaceAllIndexed { i, option ->
                                option.copy(isSelected = i == index)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Date & Time
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Ngày khám", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = "${detail.selectedTime.time}    ${detail.selectedTime.date}",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Note
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Lời nhắn cho bác sĩ:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = detail.note,
                    onValueChange = {},
                    placeholder = { Text("Nhập lời nhắn...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF1E2F2),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Price Info
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                RowBetween("Giá tại phòng khám", "${detail.priceInfo.clinicPrice}đ")
                RowBetween("Voucher dịch vụ", "-${detail.priceInfo.voucherValue}đ")
                RowBetween("Giá dịch vụ", "${detail.priceInfo.servicePrice}đ")
                RowBetween("Tạm tính giá tiền", "${detail.priceInfo.totalPrice}đ", highlight = true)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // 8. Đặt dịch vụ
        Button(
            onClick = onBookClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(50.dp)
        ) {
            Text(
                "Đặt dịch vụ",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
        }
    }
}

@Composable
fun MethodItem(title: String, address: String, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) Color(0xFFE0F7FA) else Color.White
    val borderColor = if (selected) Color.Cyan else Color.LightGray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(bgColor, shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(address, color = Color.Gray, fontSize = 13.sp)
    }
}

fun <T> SnapshotStateList<T>.replaceAllIndexed(transform: (index: Int, T) -> T) {
    val newList = this.mapIndexed(transform)
    clear()
    addAll(newList)
}

@Composable
fun RowBetween(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            value,
            color = if (highlight) Color.Blue else Color.Black,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun WriteReviewPreview() {
    val appointmentDetail = AppointmentDetail(
        doctor = DoctorInfo(
            name = "Dương Văn Lực",
            specialization = "Sức khỏe sinh sản",
            avatar = R.drawable.img,
            pricePerHour = "0 đ/giờ"
        ),
        patient = PatientInfo(
            fullName = "Nguyễn Văn Tèo",
            gender = "Nam",
            dateOfBirth = "11/12/2000",
            phoneNumber = "0909124567"
        ),
        methodOptions = listOf(
            MethodOption(
                title = "Khám tại phòng khám",
                address = "Số 69, đường 96, phường TCH, quận 13, tp HCM",
                isSelected = true
            ),
            MethodOption(
                title = "Khám tại nhà",
                address = "Số 6, đường 6, phường TCH, quận 12, tp HCM",
                isSelected = false
            )
        ),
        selectedTime = AppointmentTime(
            date = "31/3/2025",
            time = "20:00"
        ),
        note = "",
        priceInfo = PriceInfo(
            clinicPrice = 0,
            voucherValue = 15000,
            servicePrice = 0
        )
    )

    AppointmentDetailScreen(detail = appointmentDetail)
}
