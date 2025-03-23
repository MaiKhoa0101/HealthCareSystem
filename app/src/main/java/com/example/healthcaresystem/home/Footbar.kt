package com.example.healthcaresystem.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import kotlinx.serialization.Serializable

@Composable
fun CircleButton(
    onClick: () -> Unit,
    backgroundColor: Color = Color.Cyan,
    iconColor: Color = Color.White,
    size: Dp = 56.dp,
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
            Icons.Default.Add,
            contentDescription = "Add",
            tint = iconColor,
            modifier = Modifier.size(size * 0.8f)
        )
    }
}

@Composable
fun Menu() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.Cyan)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoxItem(color = Color.Cyan, text = "Trang chủ", icon = Icons.Default.Home)
                BoxItem(color = Color.Cyan, text = "Tìm kiếm", icon = Icons.Default.Search)
                Spacer(modifier = Modifier.width(56.dp))
                BoxItem(color = Color.Cyan, text = "Thông báo", icon = Icons.Default.MailOutline)
                BoxItem(color = Color.Cyan, text = "Cá nhân", icon = Icons.Default.Person)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-37).dp)
        ) {
            CircleButton(onClick = {})
        }
    }
}

@Composable
fun BoxItem(
    modifier: Modifier = Modifier,
    color: Color,
    width: Dp = 80.dp,
    height: Dp = 70.dp,
    text: String,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .size(width, height)
            .background(color = color)
            .clickable {  },
        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = "home", Modifier.size(24.dp))
            Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}