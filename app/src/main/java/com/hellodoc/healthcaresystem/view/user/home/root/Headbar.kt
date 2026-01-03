package com.hellodoc.healthcaresystem.view.user.home.root

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

fun shortenUserName(fullName: String): String {
    val parts = fullName.trim().split("\\s+".toRegex())
    return if (parts.size >= 2) {
        val firstInitial = parts.first().first().uppercaseChar()
        val lastName = parts.last()
        "$firstInitial. $lastName"
    } else {
        fullName // không cần rút gọn nếu chỉ có 1 từ
    }
}

@Composable
fun HeadBar() {
    val context = LocalContext.current
    val userViewModel: UserViewModel = hiltViewModel()
    val you by userViewModel.you.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getYou(context)
        println("You trong headbar: $you")   // sẽ chạy mỗi lần you thay đổi

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced Logo with better visibility
            Image(
                painter = painterResource(id = R.drawable.logo_hellodoc),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(60.dp)
                    .shadow(4.dp, CircleShape, clip = false)
                    .clip(CircleShape)
            )



            Spacer(modifier = Modifier.width(16.dp))

            // Premium Greeting with high contrast text
            Column {
                Text(
                    text = "HelloDoc",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 1.sp
                    )
                )
                val shortName = you?.name?.let { shortenUserName(it) } ?: "Guest"
                Text(
                    text = "Xin chào, $shortName",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // Optional: Add a notification or search icon here if needed
        }
    }
}

fun truncateName(name: String, maxLength: Int): String {
    return if (name.length > maxLength) {
        name.take(maxLength) + "..."
    } else {
        name
    }
}