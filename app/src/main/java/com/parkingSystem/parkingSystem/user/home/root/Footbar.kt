package com.parkingSystem.parkingSystem.user.home.root

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
fun FootBar(currentRoute: String?,navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom bar with icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)

                .padding(horizontal = 16.dp)
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top,

        ) {
            BoxItem(nameRoute = "Trang chủ", icon = "trangchu", nameDirection = "home",  navHostController,currentRoute)
            BoxItem(nameRoute = "Lịch hẹn", icon = "lichhen","appointment", navHostController,currentRoute)
            Spacer(modifier = Modifier.width(50.dp)) // Space for the floating button
            BoxItem(nameRoute = "Thông báo", icon = "thongbao","notification", navHostController,currentRoute)
            BoxItem(nameRoute = "Cá nhân", icon = "canhan","personal", navHostController,currentRoute)
        }

        // Floating button in the center
        CircleButton(
            onClick = {navHostController.navigate("create_post")},
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -30.dp), // Elevate the button
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

    // Gradient nhiều màu chạy quanh border
    val brush = Brush.sweepGradient(
        colors = listOf(
            Color.Magenta,
            Color.Cyan,
            Color.Green,
            Color.Yellow,
            Color.Red,
            Color.Blue,
            Color.Magenta // nối liền
        ),
        center = Offset.Zero
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = angle // xoay gradient
            }
            .border(
                width = 4.dp,
                brush = brush,
                shape = CircleShape
            )
            .graphicsLayer {
                rotationZ = -angle // giữ icon + background không xoay theo
            }
            .background(backgroundColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.onBackground,
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
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(backgroundColor)
            .size(70.dp)
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
            modifier = Modifier.height(20.dp),
            tint = if (!isSelected) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.onBackground
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = nameRoute,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (!isSelected) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.onBackground
            }        )
    }
}


