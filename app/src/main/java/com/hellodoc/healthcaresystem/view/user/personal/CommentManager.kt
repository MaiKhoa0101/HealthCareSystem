package com.hellodoc.healthcaresystem.view.user.personal

import android.content.SharedPreferences
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
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lịch sử bình luận", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Trở lại",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(end = 20.dp)
                                .clickable {
                                    navHostController.popBackStack()
                                }
                        )
                    }
                }
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
                CommentCard(comment = comment, dateText = dateText, navHostController = navHostController)
            }
        }
    }
}

@Composable
fun CommentCard(comment: ManagerResponse, dateText: String, navHostController: NavHostController) {
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
