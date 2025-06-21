package com.hellodoc.healthcaresystem.user.personal

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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.responsemodel.ManagerResponse
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteHistoryScreen(navHostController: NavHostController, sharedPreferences: SharedPreferences) {
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val userFavorites by postViewModel.userFavorites.collectAsState()

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
    }

    LaunchedEffect(userId) {
        postViewModel.getPostFavoriteByUserId(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
//                    Text(
//                        "Bài viết đã thích",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bài viết đã thích", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Trở lại",
                            fontSize = 16.sp,
                            color = Color.Blue,
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(userFavorites) { favorite ->
                val dateText = formatVietnameseDate(favorite.createdAt)
                FavoriteCard(favorite = favorite, dateText = dateText)
            }
        }
    }
}

@Composable
fun FavoriteCard(favorite: ManagerResponse, dateText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Ảnh bài viết (nếu có)
            if (favorite.post.media.isNotEmpty()) {
                AsyncImage(
                    model = favorite.post.media[0],
                    contentDescription = "Ảnh bài viết",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Nội dung bài viết
            Text(
                text = favorite.post.content,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = favorite.user.avatarURL ?: "",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = favorite.user.name,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = dateText,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

        }
    }
}


