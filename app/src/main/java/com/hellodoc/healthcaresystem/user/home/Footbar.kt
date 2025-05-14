package com.hellodoc.healthcaresystem.user.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R

@Composable
fun FootBar(currentRoute: String?,navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom bar with icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Cyan)
                .padding(horizontal = 16.dp).padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top,

        ) {
            BoxItem(nameRoute = "Trang chủ", icon = R.drawable.ic_home, nameDirection = "home",  navHostController,currentRoute)
            BoxItem(nameRoute = "Lịch hẹn", icon = R.drawable.ic_appointment,"appointment", navHostController,currentRoute)
            Spacer(modifier = Modifier.width(50.dp)) // Space for the floating button
            BoxItem(nameRoute = "Thông báo", icon = R.drawable.ic_notification,"notification", navHostController,currentRoute)
            BoxItem(nameRoute = "Cá nhân", icon = R.drawable.ic_personal,"personal", navHostController,currentRoute)
        }

        // Floating button in the center
        CircleButton(
            onClick = {navHostController.navigate("create_post")},
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -30.dp), // Elevate the button
            backgroundColor = Color(0xFF00C5CB)
        )
    }
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    backgroundColor: Color = Color(0xFF00C5CB),
    iconColor: Color = Color.White,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 5.dp, shape = CircleShape)
            .background(backgroundColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = iconColor,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
fun BoxItem(
    nameRoute: String,
    icon: Int,
    nameDirection:String,
    navHostController: NavHostController,
    currentRoute:String?,
    visible: Boolean =true,
    modifier: Modifier = Modifier
) {
    // Animate background color based on selection
    val backgroundColor by animateColorAsState(
        targetValue = if (currentRoute == nameDirection)
            MaterialTheme.colorScheme.background
        else
            Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        Column(
            modifier = modifier
                .clip(CircleShape)
                .background(backgroundColor)
                .size(70.dp)
                .clickable {
                    println(nameDirection)
                    if (nameDirection.isNotEmpty()) {
                        navHostController.navigate(nameDirection)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "",
                modifier = Modifier.height(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nameRoute,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

