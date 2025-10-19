package com.parkingSystem.parkingSystem.user.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.responsemodel.PostResponse
import com.parkingSystem.parkingSystem.responsemodel.User
import com.parkingSystem.parkingSystem.skeleton.discordClick
import com.parkingSystem.parkingSystem.viewmodel.PostViewModel
import kotlinx.coroutines.launch
@Composable
fun InteractPostManager(
    navHostController:NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    user: User,
    totalFavorites: Int = 0
) {
    val sizeButton = 28.dp
    val coroutineScope = rememberCoroutineScope()
    var showCommentSheet by remember { mutableStateOf(false) }



    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val serverIsFavorited = isFavoritedMap[post.id] ?: false
    var localFavorited by remember(post.id) { mutableStateOf(serverIsFavorited) }

    LaunchedEffect(serverIsFavorited) {
        localFavorited = serverIsFavorited
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box (
            modifier = Modifier
                .size(height = sizeButton*2, width = sizeButton*6)
                .clip(RoundedCornerShape(20.dp))
                .discordClick {
                    localFavorited = !localFavorited
                    coroutineScope.launch {
                        println("Nút like được nhấn")
                        postViewModel.updateFavoriteForPost(
                            postId = post.id,
                            userFavouriteId = user.id,
                            userFavouriteModel = user.role
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ){
            LikeButton(
                size = sizeButton,
                isFavorited = localFavorited,
                totalFavorites = totalFavorites
            )
        }

        Box (
            modifier = Modifier
                .size(height = sizeButton*2, width = sizeButton*6)
                .clip(RoundedCornerShape(20.dp))
                .clickable{
                    showCommentSheet = true
                },
            contentAlignment = Alignment.Center
        ) {
            CommentButton(
                size = sizeButton,
            )
        }


        if (showCommentSheet) {
            FullScreenCommentUI(
                navHostController = navHostController, // hoặc truyền vào từ tham số
                postId = post.id,
                onClose = { showCommentSheet = false },
                postViewModel = postViewModel,
                currentUser = user
            )
        }

    }
}

@Composable
fun LikeButton(
    size: Dp,
    isFavorited: Boolean,
    totalFavorites: Int,
    modifier: Modifier = Modifier
) {
    Box (
        contentAlignment = Alignment.Center
    ){
        Icon(
            painter = if (isFavorited) {
                painterResource(R.drawable.liked)
            } else {
                painterResource(R.drawable.like)
            },
            tint = if (isFavorited) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            contentDescription = if (isFavorited) "Unlike" else "Like",
            modifier = modifier
                .size(size+10.dp)
        )
        Text(
            totalFavorites.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
            )
    }
}

@Composable
fun CommentButton(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(R.drawable.comment),
        contentDescription = "Comment",
        modifier = modifier
            .size(size)
    )
}