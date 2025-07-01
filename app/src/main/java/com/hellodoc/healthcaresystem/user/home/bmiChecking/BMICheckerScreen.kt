package com.hellodoc.healthcaresystem.user.home.bmiChecking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun BMICheckerScreen(navHostController: NavHostController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf<Double?>(null) }

    val bmiResult = bmi?.let {
        when {
            it < 18.5 -> "Thiếu cân"
            it < 24.9 -> "Bình thường"
            it < 29.9 -> "Thừa cân"
            else -> "Béo phì"
        }
    }

    val bmiAdvice  = bmi?.let {
        when {
            it < 18.5 -> "Bạn đang thiếu cân. Hãy ăn uống đủ chất và luyện tập đều đặn."
            it < 24.9 -> "Chúc mừng! Bạn có cân nặng lý tưởng. Hãy duy trì lối sống lành mạnh."
            it < 29.9 -> "Bạn đang thừa cân. Hãy xem xét chế độ ăn và vận động nhiều hơn."
            else -> "Bạn béo phì. Nên tham khảo ý kiến bác sĩ để có lộ trình giảm cân phù hợp."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
//            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TopBar(title = "Kiểm tra BMI",onClick = { navHostController.popBackStack() })
//        Text(
//            text = "Kiểm tra BMI",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        )

        // Nhập cân nặng
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(text = "Cân nặng:")
            CapsuleInput(label = "kg", value = weight, onValueChange = { weight = it })
        }

        // Nhập chiều cao
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(text = "Chiều cao:")
            CapsuleInput(label = "cm", value = height, onValueChange = { height = it })
        }

        // Nút tính BMI
        Button(
            onClick = {
                val weightVal = weight.toDoubleOrNull()
                val heightVal = height.toDoubleOrNull()
                if (weightVal != null && weightVal > 0 && heightVal != null && heightVal > 0) {
                    val heightMeters = heightVal / 100
                    bmi = weightVal / (heightMeters * heightMeters)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Nhận kết quả")
        }

        // Kết quả
        bmi?.let {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Kết quả", style = MaterialTheme.typography.titleMedium)
                // Viên nhộng BMI
                CapsuleResult(label = "BMI:", value = String.format("%.2f", it))
                Text("Nhận xét: $bmiResult")
                Text("Lời khuyên: $bmiAdvice")
            }
        }
    }
}

@Composable
fun TopBar(title: String,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
            .height(56.dp)
    ) {
        // Nút quay lại
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() }
        )

        // Tiêu đề ở giữa
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun CapsuleInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .width(170.dp)
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            TextField(
                value = value,
                onValueChange = {
                    // Chỉ cho phép nhập số dương (số thực hoặc nguyên)
                    val filtered = it.filter { ch -> ch.isDigit() || ch == '.' }
                    if (!filtered.startsWith("-")) {
                        onValueChange(filtered)
                    }
                },
                placeholder = { Text("Nhập số") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(100.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label)
        }
    }
}


@Composable
fun CapsuleResult(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .width(150.dp)
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
