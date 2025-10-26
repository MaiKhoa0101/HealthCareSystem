package com.parkingSystem.parkingSystem.admin

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme

@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    AdminDashboardScreen()
}

@Composable
fun AdminDashboardScreen() {
    val gradient: Brush = LocalGradientTheme.current.primary
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Parking Management System",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ParkingLotStatusSection()
            Spacer(modifier = Modifier.height(16.dp))
            RevenueReportScreen()
            Spacer(modifier = Modifier.height(16.dp))
            VehicleTypeRevenueCard()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ParkingLotStatusSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Overview",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                value = "44",
                title = "Parked Vehicles",
                backgroundColor = Brush.linearGradient(
                    colors = listOf(Color(0xFF36D1DC), Color(0xFF5B86E5))
                ),
                icon = R.drawable.submit_arrow,
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                value = "53",
                title = "Available Spots",
                backgroundColor = Brush.linearGradient(
                    colors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
                ),
                icon = R.drawable.submit_arrow,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun InfoCard(
    value: String,
    title: String,
    backgroundColor: Brush,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = value,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Details",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueReportScreen() {
    var selectedFilter by remember { mutableStateOf("Month") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Revenue Report",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2D3748)
                )
                FilterDropdown(
                    options = listOf("Day", "Week", "Month", "Year"),
                    selectedOption = selectedFilter,
                    onOptionSelected = { selectedFilter = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "This Month",
                        color = Color(0xFF718096),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$12,582",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color(0xFF2D3748)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "+15%",
                                color = Color(0xFF059669),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Last Month",
                        color = Color(0xFF718096),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$10,941",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF2D3748)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "↑ 15%",
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Compared to last month",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun VehicleTypeRevenueCard() {
    var selectedFilter by remember { mutableStateOf("Bike") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Revenue by Vehicle Type",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2D3748)
                )
                FilterDropdown(
                    options = listOf("Bike", "Car", "MotorBike"),
                    selectedOption = selectedFilter,
                    onOptionSelected = { selectedFilter = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val revenueData = when (selectedFilter) {
                "Bike" -> mapOf(
                    "Jan" to 2500f, "Feb" to 3200f, "Mar" to 2800f,
                    "Apr" to 4100f, "May" to 3800f, "Jun" to 4500f
                )
                "Car" -> mapOf(
                    "Jan" to 8500f, "Feb" to 9200f, "Mar" to 8800f,
                    "Apr" to 10100f, "May" to 9800f, "Jun" to 11500f
                )
                else -> mapOf(
                    "Jan" to 5500f, "Feb" to 6200f, "Mar" to 5800f,
                    "Apr" to 7100f, "May" to 6800f, "Jun" to 7500f
                )
            }

            val totalRevenue = revenueData.values.sum()
            val avgRevenue = totalRevenue / revenueData.size
            val maxValue = revenueData.values.maxOrNull() ?: 1f
            val minValue = revenueData.values.minOrNull() ?: 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBox(
                    label = "Total Revenue",
                    value = "${String.format("%.0f", totalRevenue)}",
                    color = Color(0xFF3B82F6)
                )
                StatBox(
                    label = "Average",
                    value = "${String.format("%.0f", avgRevenue)}",
                    color = Color(0xFF10B981)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val range = maxValue - minValue
                    val points = revenueData.values.mapIndexed { index, value ->
                        val x = if (revenueData.size > 1) {
                            (size.width / (revenueData.size - 1)) * index
                        } else size.width / 2
                        val normalizedValue = if (range > 0) (value - minValue) / range else 0.5f
                        val y = size.height - (size.height * 0.15f) - (normalizedValue * size.height * 0.7f)
                        Pair(x, y)
                    }

                    for (i in 0..4) {
                        val y = size.height * (i / 4f)
                        drawLine(
                            color = Color(0xFFE5E7EB),
                            start = androidx.compose.ui.geometry.Offset(0f, y),
                            end = androidx.compose.ui.geometry.Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (points.size > 1) {
                        val gradientPath = Path().apply {
                            moveTo(0f, size.height)
                            lineTo(points.first().first, points.first().second)
                            for (i in 0 until points.size - 1) {
                                val current = points[i]
                                val next = points[i + 1]
                                val cp1X = current.first + (next.first - current.first) / 3
                                val cp2X = current.first + 2 * (next.first - current.first) / 3
                                cubicTo(cp1X, current.second, cp2X, next.second, next.first, next.second)
                            }
                            lineTo(size.width, size.height)
                            close()
                        }
                        drawPath(
                            path = gradientPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6).copy(alpha = 0.3f),
                                    Color(0xFF3B82F6).copy(alpha = 0.05f)
                                )
                            )
                        )

                        // Line path
                        val linePath = Path().apply {
                            moveTo(points.first().first, points.first().second)
                            for (i in 0 until points.size - 1) {
                                val current = points[i]
                                val next = points[i + 1]
                                val cp1X = current.first + (next.first - current.first) / 3
                                val cp2X = current.first + 2 * (next.first - current.first) / 3
                                cubicTo(cp1X, current.second, cp2X, next.second, next.first, next.second)
                            }
                        }
                        drawPath(
                            path = linePath,
                            color = Color(0xFF3B82F6),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    points.forEach { point ->
                        drawCircle(
                            color = Color.White,
                            radius = 8.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                        )
                        drawCircle(
                            color = Color(0xFF3B82F6),
                            radius = 5.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                revenueData.keys.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        textAlign = when (index) {
                            0 -> TextAlign.Start
                            revenueData.size - 1 -> TextAlign.End
                            else -> TextAlign.Center
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            label,
            color = Color(0xFF6B7280),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = color
        )
    }
}

@Composable
fun FilterDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F4F6))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                selectedOption,
                color = Color(0xFF4F46E5),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color(0xFF4F46E5),
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = if (option == selectedOption) Color(0xFF4F46E5) else Color(0xFF374151),
                            fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}