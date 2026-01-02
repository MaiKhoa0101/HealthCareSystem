package com.hellodoc.healthcaresystem.view.user.personal

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ManagerResponse
import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentHistoryScreen(navHostController: NavHostController) {
    var userId by remember { mutableStateOf("") }
    val postViewModel: PostViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttribute("userId", context)
    }

    val userComments by postViewModel.userComments.collectAsState()

    LaunchedEffect(userId) {
        postViewModel.getPostCommentByUserId(userId)
    }

    Scaffold(
        topBar = {
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
                        onClick = { navHostController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Lịch sử bình luận",
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(userComments) { comment ->
                val dateText = formatVietnameseDate(comment.createdAt)
                CommentCard(comment = comment, dateText = dateText, navHostController = navHostController)
            }
        }
    }
}

@Composable
fun CommentCard(comment: ManagerResponse, dateText: String, navHostController: NavHostController) {
    if (comment == null) {
        Log.w("CommentCard", "Nhận được comment null, bỏ qua render")

        return
    }

    // Kiểm tra post có null không
    val post = comment.post
    if (post == null) {
        Log.w("CommentCard", "Post null trong comment, bỏ qua render")
        return
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navHostController.navigate("post-detail/${comment.post.id}")
            }
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // Hình ảnh bài viết
            AsyncImage(
                model = comment.post.media?.getOrNull(0) ?: "",
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
                        .clickable{

                        },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = comment.user.name, fontWeight = FontWeight.SemiBold)
                    Text(text = dateText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
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
