package com.hellodoc.healthcaresystem.user.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.user.notification.timeAgoInVietnam
import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsDetailScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("your_pref_name", Context.MODE_PRIVATE)

    val viewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })

    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val adminId = jwt?.getClaim("userId")?.asString() ?: ""
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    val news = savedStateHandle?.get<NewsResponse>("selectedNews")

    if (news == null) {
        Text("Không tìm thấy tin tức.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.Black
                )
            }
            Text(
                text = "Tin tức chi tiết",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Ảnh bài viết (nếu có)
        if (news.media.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(news.media[0]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tiêu đề
        Text(
            text = news.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Thông tin người đăng: Admin (giả định avatar)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.heart),
                contentDescription = "Admin Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("Admin đẹp trai ngầu lòi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = news.createdAt.timeAgoInVietnam(), fontSize = 13.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Like và Comment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(onClick = { /* TODO: like */ }) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Thích",
                    tint = Color.Red
                )
            }

            IconButton(onClick = { /* TODO: mở comment */ }) {
                Icon(
                    imageVector = Icons.Default.ModeComment,
                    contentDescription = "Bình luận",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nội dung bài viết
        Text(
            text = news.content,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}