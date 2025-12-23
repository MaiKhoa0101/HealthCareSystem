package com.hellodoc.healthcaresystem.view.user.home.bmiChecking

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R


@Composable
fun BMICheckerScreen(navHostController: NavHostController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf<Double?>(null) }
    val scrollState = rememberScrollState()

    val bmiInfo = bmi?.let { getBMIInfo(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
    ) {
        TopBar(title = "Kiểm tra BMI", onClick = { navHostController.popBackStack() })

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Intro Section
            Text(
                text = "Tính toán chỉ số khối cơ thể (BMI) của bạn để theo dõi tình trạng sức khỏe.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Input Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Thông tin của bạn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = filterNumbers(it) },
                        label = { Text("Cân nặng (kg)") },
                        placeholder = { Text("Ví dụ: 65") },
                        leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null) },
                        suffix = { Text("kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = filterNumbers(it) },
                        label = { Text("Chiều cao (cm)") },
                        placeholder = { Text("Ví dụ: 170") },
                        leadingIcon = { Icon(Icons.Default.Height, contentDescription = null) },
                        suffix = { Text("cm") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val w = weight.toDoubleOrNull()
                            val h = height.toDoubleOrNull()
                            if (w != null && h != null && h > 0) {
                                val hMet = h / 100
                                bmi = w / (hMet * hMet)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tính chỉ số BMI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // Results Section
            AnimatedVisibility(
                visible = bmi != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                bmiInfo?.let { info ->
                    ResultCard(bmi = bmi!!, info = info)
                }
            }
        }
    }
}

data class BMIInfo(
    val category: String,
    val advice: String,
    val color: Color,
    val rangeValue: Float // 0..1 for progress bar
)

fun getBMIInfo(bmi: Double): BMIInfo {
    return when {
        bmi < 18.5 -> BMIInfo("Thiếu cân", "Bạn đang thiếu cân. Hãy ăn uống đủ chất và luyện tập đều đặn.", Color(0xFF03A9F4), (bmi / 40).toFloat().coerceIn(0f, 1f))
        bmi < 24.9 -> BMIInfo("Bình thường", "Chúc mừng! Bạn có cân nặng lý tưởng. Hãy duy trì lối sống lành mạnh.", Color(0xFF4CAF50), (bmi / 40).toFloat().coerceIn(0f, 1f))
        bmi < 29.9 -> BMIInfo("Thừa cân", "Bạn đang thừa cân. Hãy xem xét chế độ ăn và vận động nhiều hơn.", Color(0xFFFFC107), (bmi / 40).toFloat().coerceIn(0f, 1f))
        else -> BMIInfo("Béo phì", "Bạn béo phì. Nên tham khảo ý kiến bác sĩ để có lộ trình giảm cân phù hợp.", Color(0xFFF44336), (bmi / 40).toFloat().coerceIn(0f, 1f))
    }
}

fun filterNumbers(input: String): String {
    return input.filter { it.isDigit() || it == '.' }
}

@Composable
fun ResultCard(bmi: Double, info: BMIInfo) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = info.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kết quả của bạn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Circular/Modern BMI Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = info.color.copy(alpha = 0.2f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = info.color,
                        startAngle = 135f,
                        sweepAngle = (info.rangeValue * 270f),
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", bmi),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = info.color
                    )
                    Text("BMI", style = MaterialTheme.typography.labelMedium)
                }
            }

            Surface(
                color = info.color,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = info.category,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = info.advice,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // BMI Scale Indicator
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("18.5", style = MaterialTheme.typography.labelSmall)
                    Text("25.0", style = MaterialTheme.typography.labelSmall)
                    Text("30.0", style = MaterialTheme.typography.labelSmall)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF03A9F4),
                                    Color(0xFF4CAF50),
                                    Color(0xFFFFC107),
                                    Color(0xFFF44336)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun TopBar(title: String, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().shadow(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// Capsule functions removed as they are replaced by modern Card-based components.
