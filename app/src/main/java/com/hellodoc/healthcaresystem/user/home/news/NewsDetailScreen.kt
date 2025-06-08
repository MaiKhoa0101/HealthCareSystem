package com.hellodoc.healthcaresystem.user.home.news

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
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.post.FullScreenCommentNews
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
import kotlin.collections.get


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsDetailScreen(
    navHostController: NavHostController,
    viewModel: NewsViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//    val viewModel: NewsViewModel = viewModel(factory = viewModelFactory {
//        initializer { NewsViewModel(sharedPreferences) }
//    })

    val token = sharedPreferences.getString("access_token", null)
    val jwt = remember(token) {
        runCatching { JWT(token ?: "") }.getOrNull()
    }
    val currentUserId = jwt?.getClaim("userId")?.asString() ?: ""
    val currentUserModel = if (sharedPreferences.getString("role", "") == "user") "User" else "Doctor"

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    val news = savedStateHandle?.get<NewsResponse>("selectedNews")
    Log.d("NewsDetail", "Loaded news: ${news?.title}, id: ${news?.id}")


    val isFavoritedMap by viewModel.favoriteMap.collectAsState()
    val totalFavoritesMap by viewModel.favoriteCountMap.collectAsState()
    val isFavorited = isFavoritedMap[news?.id] ?: false
    val totalFavorites = totalFavoritesMap[news?.id] ?: "0"

    var showFullScreenComment by remember { mutableStateOf(false) }

    LaunchedEffect(jwt) {
        println("==== JWT PAYLOAD ====")
        println("Raw token: $token")
        println("User ID: ${jwt?.getClaim("userId")?.asString()}")
        println("Role: ${jwt?.getClaim("role")?.asString()}")
        println("Email: ${jwt?.getClaim("email")?.asString()}")
        println("Full JWT: ${jwt?.claims}")
    }

    LaunchedEffect(news?.id) {
        news?.id?.let { viewModel.getFavorite(it, currentUserId) }
        news?.id?.let { viewModel.getComments(it) }
    }

    if (news == null) {
        Text("Không tìm thấy tin tức.")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Header & Back
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                }
                Text("Tin tức chi tiết", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
        }

        item {
            if (news.media.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(news.media[0]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(10.dp)),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Text(news.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Image(painter = rememberAsyncImagePainter(R.drawable.heart), contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape))
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("Admin:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(news.createdAt.timeAgoInVietnam(), fontSize = 13.sp, color = Color.Gray)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        Log.d("NewsDetail", "Nút like được nhấn")
                        news?.id?.let {
                            viewModel.toggleFavoriteNews(it, currentUserId, currentUserModel)
                        }
                    }) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Thích",
                        tint = if (isFavorited) Color.Red else Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$totalFavorites")
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    showFullScreenComment = true
                }) {
                    Icon(Icons.Default.ModeComment, contentDescription = "Bình luận", tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bình luận")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(news.content, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }

    if (showFullScreenComment && news != null) {
        FullScreenCommentNews(
            navHostController = navHostController,
            newsId = news.id,
            onClose = { showFullScreenComment = false },
            newsViewModel = viewModel,
            currentUserId = currentUserId,
            currentUserModel = currentUserModel
        )
    }

}
