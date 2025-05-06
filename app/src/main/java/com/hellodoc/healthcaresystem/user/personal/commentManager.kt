package com.hellodoc.healthcaresystem.user.personal

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.responsemodel.ManagerResponse
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(sharedPreferences: SharedPreferences) {
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
    }

    val userComments by postViewModel.userComments.collectAsState()

    LaunchedEffect(userId) {
        postViewModel.getPostCommentByUserId(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử bình luận", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(userComments) { comment ->
                val dateText = formatVietnameseDate(comment.createdAt)
                CommentCard(comment = comment, dateText = dateText)
            }
        }
    }
}

@Composable
fun CommentCard(comment: ManagerResponse, dateText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Hình ảnh bài viết
            AsyncImage(
                model = comment.post.media[0] ?: "",
                contentDescription = "Ảnh bài viết",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tiêu đề bài viết
            Text(
                text = comment.post.content,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Người bình luận và ngày giờ, nội dung
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = comment.user.avatarURL ?: "",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = comment.user.name, fontWeight = FontWeight.SemiBold)
                    Text(text = dateText, fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = comment.content,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}


fun formatVietnameseDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(isoDate)
        val formatter = SimpleDateFormat("d 'Tháng' M yyyy", Locale("vi"))
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        ""
    }
}
