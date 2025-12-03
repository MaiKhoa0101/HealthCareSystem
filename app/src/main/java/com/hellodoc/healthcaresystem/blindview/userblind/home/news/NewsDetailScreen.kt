package com.hellodoc.healthcaresystem.blindview.userblind.home.news

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NewsResponse
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.view.user.notification.timeAgoInVietnam
import com.hellodoc.healthcaresystem.view.user.post.FullScreenCommentNews
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
import kotlin.collections.get


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsDetailScreen(
    navHostController: NavHostController
) {
    val newsViewModel: NewsViewModel = hiltViewModel()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val token = sharedPreferences.getString("access_token", null)
    val jwt = remember(token) {
        runCatching { JWT(token ?: "") }.getOrNull()
    }
    val currentUserId = jwt?.getClaim("userId")?.asString() ?: ""
    val currentUserModel: String = sharedPreferences.getString("role", "") ?: "User"

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    val news = savedStateHandle?.get<NewsResponse>("selectedNews")
    Log.d("NewsDetail", "Loaded news: ${news?.title}, id: ${news?.id}")


    val isFavoritedMap by newsViewModel.favoriteMap.collectAsState()
    val totalFavoritesMap by newsViewModel.favoriteCountMap.collectAsState()
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
        news?.id?.let { newsViewModel.getFavorite(it, currentUserId) }
        news?.id?.let { newsViewModel.getComments(it) }
    }

    if (news == null) {
        Text("Không tìm thấy tin tức.")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Header & Back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 16.dp, horizontal = 10.dp)
                    .padding(end = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = MaterialTheme.colorScheme.onBackground)
                }
                Text("Tin tức chi tiết",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 8.dp),
                )
            }
        }

        item {
            if (news.media.isNotEmpty()) {
                println("ko empty anh")
                Image(
                    painter = rememberAsyncImagePainter(news.media[0]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(10.dp)),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Text(news.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.heart),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Admin:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            news.createdAt.timeAgoInVietnam(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp)
                            .clickable {
                                Log.d("NewsDetail", "Nút like được nhấn")
                                news?.id?.let {
                                    newsViewModel.toggleFavoriteNews(
                                        it,
                                        currentUserId,
                                        currentUserModel
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(44.dp),
                            imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Thích",
                            tint = if (isFavorited) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$totalFavorites")
                    }
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                showFullScreenComment = true
                            }
                    ) {
                        Icon(
                            Icons.Default.ModeComment,
                            contentDescription = "Bình luận",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bình luận")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(news.content, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }

    if (showFullScreenComment && news != null) {
        FullScreenCommentNews(
            navHostController = navHostController,
            newsId = news.id,
            onClose = { showFullScreenComment = false },
            newsViewModel = newsViewModel,
            currentUserId = currentUserId,
            currentUserModel = currentUserModel
        )
    }

}
