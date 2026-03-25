package com.hellodoc.healthcaresystem.view.user.home.bmiChecking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            TopBar(title = "Kiểm tra sức khỏe", onClick = { navHostController.popBackStack() })

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Label
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Chỉ số khối cơ thể (BMI)",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Theo dõi cân nặng để có sức khỏe tốt hơn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Input Section - Glassmorphic look
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        BMIRowInput(
                            value = weight,
                            onValueChange = { weight = filterNumbers(it) },
                            label = "Cân nặng",
                            suffix = "kg",
                            icon = Icons.Default.MonitorWeight
                        )

                        BMIRowInput(
                            value = height,
                            onValueChange = { height = filterNumbers(it) },
                            label = "Chiều cao",
                            suffix = "cm",
                            icon = Icons.Default.Height
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
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Tính toán ngay", fontWeight = FontWeight.Black, fontSize = 17.sp)
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
                
                Spacer(modifier = Modifier.height(80.dp)) // Extra space at bottom
            }
        }
    }
}

@Composable
fun BMIRowInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)) },
            leadingIcon = { 
                Surface(
                    modifier = Modifier.padding(start = 8.dp).size(36.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
            },
            suffix = { 
                Surface(
                    modifier = Modifier.padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = suffix, 
                        fontWeight = FontWeight.Black, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun TopBar(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
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
                .height(64.dp)
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

data class BMIInfo(
    val category: String,
    val advice: String,
    val color: Color,
    val progress: Float
)

fun getBMIInfo(bmi: Double): BMIInfo {
    return when {
        bmi < 18.5 -> BMIInfo("Thiếu cân", "Cơ thể bạn đang ở mức thiếu cân. Hãy bổ sung thêm dinh dưỡng.", Color(0xFF3498DB), (bmi / 40).toFloat().coerceIn(0f, 1f))
        bmi < 24.9 -> BMIInfo("Bình thường", "Bạn có một thân hình lý tưởng. Hãy tiếp tục duy trì nhé!", Color(0xFF2ECC71), (bmi / 40).toFloat().coerceIn(0f, 1f))
        bmi < 29.9 -> BMIInfo("Thừa cân", "Ái chà! Hơi thừa cân một chút rồi. Hãy chăm chỉ vận động nhé.", Color(0xFFF1C40F), (bmi / 40).toFloat().coerceIn(0f, 1f))
        else -> BMIInfo("Béo phì", "Cảnh báo béo phì! Bạn nên điều chỉnh chế độ ăn uống và tập luyện.", Color(0xFFE74C3C), (bmi / 40).toFloat().coerceIn(0f, 1f))
    }
}

fun filterNumbers(input: String): String {
    return input.filter { it.isDigit() || it == '.' }
}

@Composable
fun ResultCard(bmi: Double, info: BMIInfo) {
    val progressAnim by animateFloatAsState(
        targetValue = info.progress,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing), label = "bmiProgress"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Phân tích kết quả",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )

            // Dynamic Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Background Track
                Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Progress Track
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to Color(0xFF3498DB),
                            0.25f to Color(0xFF2ECC71),
                            0.5f to Color(0xFFF1C40F),
                            0.75f to Color(0xFFE74C3C),
                            1f to Color(0xFFE74C3C)
                        ),
                        startAngle = 135f,
                        sweepAngle = progressAnim * 270f,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", bmi),
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = info.color
                    )
                    Text(
                        text = "BMI",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Status Badge
            Surface(
                color = info.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, info.color)
            ) {
                Text(
                    text = info.category,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    color = info.color,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Health Advice Box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = info.color,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = info.advice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            // BMI Scale Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem("Gầy", Color(0xFF3498DB))
                LegendItem("BT", Color(0xFF2ECC71))
                LegendItem("Thừa", Color(0xFFF1C40F))
                LegendItem("Béo", Color(0xFFE74C3C))
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
