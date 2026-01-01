package com.hellodoc.healthcaresystem.view.user.home.root

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.math.roundToInt

@Composable
fun FootBar(currentRoute: String?, navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(105.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Transparent gradient overlay for smooth transition
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        // The background bar with premium styling
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            // Bottom bar with icons
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BoxItem(nameRoute = "Trang chủ", icon = "trangchu", nameDirection = "home", navHostController, currentRoute)
                BoxItem(nameRoute = "Lịch hẹn", icon = "lichhen", "appointment", navHostController, currentRoute)
                
                Spacer(modifier = Modifier.width(56.dp)) // Space for FAB
                
                BoxItem(nameRoute = "Thông báo", icon = "thongbao", "notification", navHostController, currentRoute)
                BoxItem(nameRoute = "Cá nhân", icon = "canhan", "personal", navHostController, currentRoute)
            }
        }

        // Floating button - Premium brand colors
        CircleButton(
            onClick = { navHostController.navigate("create_post") },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 4.dp), 
        )
    }
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    // Animation xoay
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing), // 3s xoay 1 vòng
            repeatMode = RepeatMode.Restart
        ),
        label = "angleAnim"
    )

    // Brand-aligned gradient
    val brandBrush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFFFFD846), // HelloDocYellow
            Color(0xFFFF9F43), // Accent
            Color(0xFFFFFFFF), // Amber
            Color(0xFFFFFFFF)
        ),
        center = Offset.Zero
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(12.dp, CircleShape)
            .graphicsLayer {
                rotationZ = angle // xoay gradient
            }
            .border(
                width = 3.dp,
                brush = brandBrush,
                shape = CircleShape
            )
            .graphicsLayer {
                rotationZ = -angle // giữ icon + background không xoay theo
            }
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.Black,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable

fun BoxItem(
    nameRoute: String,
    icon: String,
    nameDirection: String,
    navHostController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val iconchange: ImageVector = when (icon) {
        "trangchu" -> Icons.Default.Home
        "lichhen" -> Icons.Default.CalendarToday
        "thongbao" -> Icons.Default.Notifications
        "canhan" -> Icons.Default.Person
        else -> Icons.Default.Add
    }

    // background animate khi tab được chọn
    val backgroundColor by animateColorAsState(
        targetValue = if (currentRoute == nameDirection)
            MaterialTheme.colorScheme.background
        else
            Color.Transparent,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    )

    // animate offset và alpha khi route thay đổi
    val isSelected = currentRoute == nameDirection

    val offsetY by animateFloatAsState(
        targetValue = if (isSelected) 0f else -20f, // tab đang chọn = vị trí chuẩn, tab khác thì trượt lên
        animationSpec = tween(500, easing = LinearOutSlowInEasing),
        label = "offsetAnim"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.8f, // tab đang chọn thì rõ nét, tab khác mờ đi
        animationSpec = tween(500, easing = LinearOutSlowInEasing),
        label = "alphaAnim"
    )

    Column(
        modifier = modifier
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .alpha(alpha)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .clickable {
                if (nameDirection.isNotEmpty()) {
                    navHostController.navigate(nameDirection) {
                        popUpTo(navHostController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = iconchange,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = nameRoute,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
            )
        )
    }
}


