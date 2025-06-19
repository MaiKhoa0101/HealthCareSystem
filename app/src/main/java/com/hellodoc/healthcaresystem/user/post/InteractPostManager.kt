package com.hellodoc.healthcaresystem.user.post

import android.app.Dialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun InteractPostManager(navHostController:NavHostController,postViewModel: PostViewModel, post: PostResponse, user: User) {
    val sizeButton = 30.dp
    val coroutineScope = rememberCoroutineScope()
    var showCommentSheet by remember { mutableStateOf(false) }


    // Lấy trạng thái favorite từ ViewModel
    LaunchedEffect(post.id, user.id) {
        postViewModel.fetchFavoriteForPost(postId = post.id, userId = user.id)
    }
    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val isFavorited = isFavoritedMap[post.id] ?: false

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box (
            modifier = Modifier
                .size(height = sizeButton, width = sizeButton*5)
                .clip(RoundedCornerShape(20.dp))
                .clickable{
                    coroutineScope.launch {
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
                isFavorited = isFavorited,
            )
        }

        Box (
            modifier = Modifier
                .size(height = sizeButton, width = sizeButton*5)
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
                currentUserId = user.id
            )
        }

    }
}

@Composable
fun LikeButton(
    size: Dp,
    isFavorited: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = if (isFavorited) {
            painterResource(R.drawable.liked)
        } else {
            painterResource(R.drawable.like)
        },
        tint = if (isFavorited) {
            androidx.compose.ui.graphics.Color.Red
        } else {
            androidx.compose.ui.graphics.Color.Black
        },
        contentDescription = if (isFavorited) "Unlike" else "Like",
        modifier = modifier
            .size(size)
    )
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