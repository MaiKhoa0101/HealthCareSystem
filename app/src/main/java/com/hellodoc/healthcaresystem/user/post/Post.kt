package com.hellodoc.healthcaresystem.user.post

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.user.home.report.ReportPostUser


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostColumn(
    navHostController: NavHostController,
    idUserOfPost: String = "",
    userWhoInteractWithThisPost: User,
    postViewModel: PostViewModel
) {
    val posts by postViewModel.posts.collectAsState()
    val hasMorePosts by postViewModel.hasMorePosts.collectAsState()
    val isLoadingMorePosts by postViewModel.isLoadingMorePosts.collectAsState()

    // Lấy bài viết đầu tiên
    LaunchedEffect(Unit) {
        if (idUserOfPost.isNotEmpty()) {
            postViewModel.getPostByUserId(idUserOfPost)
        } else {
            postViewModel.fetchPosts()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Hiển thị danh sách bài viết
        posts.forEach { post ->
            var showPostReportDialog by remember { mutableStateOf(false) }
            Box (modifier = Modifier.fillMaxWidth()) {
                Post(
                    navHostController = navHostController,
                    postViewModel = postViewModel,
                    post = post,
                    userWhoInteractWithThisPost = userWhoInteractWithThisPost,
                    onClickReport = {
                        showPostReportDialog = !showPostReportDialog
                    })
                Spacer(modifier = Modifier.height(8.dp))
                if (showPostReportDialog) {
                    ReportPostUser(
                        context = navHostController.context,
                        youTheCurrentUserUseThisApp = userWhoInteractWithThisPost,
                        userReported = post.user,
                        onClickShowPostReportDialog = { showPostReportDialog = false },
                        sharedPreferences = navHostController.context.getSharedPreferences(
                            "MyPrefs",
                            Context.MODE_PRIVATE
                        )
                    )
                }
            }
        }

        // Hiển thị loading cuối danh sách
        if (isLoadingMorePosts && hasMorePosts) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Post(
    navHostController:NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    userWhoInteractWithThisPost: User,
    onClickReport: () -> Unit
) {
    var showImageDetail by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    HorizontalDivider(thickness = 2.dp)
    Box (
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            PostHeader(
                navHostController = navHostController,
                userWhoInteractWithThisPost,
                post,
                onClickReport
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostBody(post)
            Spacer(modifier = Modifier.height(8.dp))
            PostMedia(
                post = post,
                showImageDetail = showImageDetail,
                selectedImageIndex = selectedImageIndex,
                onImageClick = { index ->
                    selectedImageIndex = index
                    showImageDetail = true
                },
                onDismiss = {
                    showImageDetail = false
                }
            )
            InteractPostManager(
                navHostController = navHostController,
                postViewModel = postViewModel,
                post = post,
                user = userWhoInteractWithThisPost
            )
        }

    }
}

@Composable
fun PostHeader(navHostController: NavHostController,userWhoInteractWithThisPost: User,post: PostResponse, onClickReport: () -> Unit){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ảnh avatar
                AsyncImage(
                    model = post.user.avatarURL,
                    contentDescription = "Avatar of ${post.user.name}",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (post.user.id != userWhoInteractWithThisPost.id) {
                                println("Id user 1: " + post.user.id)
                                navHostController.navigate("otherUserProfile/${post.user.id}")
                            } else {
                                navHostController.navigate("personal")
                            }
                        },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .clickable {
                            if (post.user.id != userWhoInteractWithThisPost.id) {
                                navHostController.navigate("otherUserProfile/${post.user.id}")
                            } else {
                                navHostController.navigate("personal")
                            }
                        },
                ) {
                    Text(
                        text = post.user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    // Có thể thêm thông tin khác như thời gian đăng
                }
            }
            IconButton(
                onClick = {
                    onClickReport()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}

@Composable
fun PostBody(post: PostResponse){
    // Nội dung bài viết
    Text(
        text = post.content,
        fontSize = 15.sp,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
fun PostMedia(
    post: PostResponse,
    showImageDetail: Boolean,
    selectedImageIndex: Int,
    onImageClick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!post.media.isNullOrEmpty()) {
        ImageGrid(
            imageUrls = post.media,
            onImageClick = { _, index ->
                onImageClick(index)
            }
        )
        Spacer(modifier = Modifier.height(6.dp))
    }

    if (showImageDetail && !post.media.isNullOrEmpty()) {
        ImageDetailDialog(
            imageUrls = post.media,
            initialIndex = selectedImageIndex,
            onDismiss = onDismiss
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGrid(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onImageClick: ((String, Int) -> Unit)? = null
) {
    val maxImagesToShow = 5
    val extraImageCount = imageUrls.size - maxImagesToShow

    when {
        imageUrls.isEmpty() -> {
            // Handle empty state
        }

        imageUrls.size == 1 -> {
            // Single image - full width with proper aspect ratio
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(8.dp), spotColor = Color.Black, ambientColor = Color.Black)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onImageClick?.invoke(imageUrls[0], 0) }
            ) {
                AsyncImage(
                    model = imageUrls[0],
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f) // Instagram-like aspect ratio
                        .background(Color.Gray.copy(alpha = 0.1f))
                )
            }
        }

        imageUrls.size == 2 -> {
            // Two images side by side
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                imageUrls.forEachIndexed { index, url ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(if (index == 0) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 0) 12.dp else 0.dp))
                            .clickable { onImageClick?.invoke(url, index) }
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Post image ${index + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f))
                        )
                    }
                }
            }
        }

        imageUrls.size == 3 -> {
            // Three images: one large on left, two stacked on right (Instagram style)
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Large image on the left
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                        .clickable { onImageClick?.invoke(imageUrls[0], 0) }
                ) {
                    AsyncImage(
                        model = imageUrls[0],
                        contentDescription = "Post image 1",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.1f))
                    )
                }

                // Two smaller images stacked on the right
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(0.dp, 12.dp, 0.dp, 0.dp))
                            .clickable { onImageClick?.invoke(imageUrls[1], 1) }
                    ) {
                        AsyncImage(
                            model = imageUrls[1],
                            contentDescription = "Post image 2",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 0.dp))
                            .clickable { onImageClick?.invoke(imageUrls[2], 2) }
                    ) {
                        AsyncImage(
                            model = imageUrls[2],
                            contentDescription = "Post image 3",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f))
                        )
                    }
                }
            }
        }

        imageUrls.size == 4 -> {
            // Four images in 2x2 grid
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 12.dp else 0.dp,
                                        topEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(imageUrls[index], index) }
                        ) {
                            AsyncImage(
                                model = imageUrls[index],
                                contentDescription = "Post image ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        val imageIndex = index + 2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = if (index == 0) 12.dp else 0.dp,
                                        bottomEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(imageUrls[imageIndex], imageIndex) }
                        ) {
                            AsyncImage(
                                model = imageUrls[imageIndex],
                                contentDescription = "Post image ${imageIndex + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    }
                }
            }
        }

        imageUrls.size >= 5 -> {
            // Five or more images: 2x2 grid with overlay on last image
            val displayedImages = imageUrls.take(maxImagesToShow)

            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // First row with 2 images
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 12.dp else 0.dp,
                                        topEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(displayedImages[index], index) }
                        ) {
                            AsyncImage(
                                model = displayedImages[index],
                                contentDescription = "Post image ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    }
                }

                // Second row with 3 images (last one with overlay)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { index ->
                        val imageIndex = index + 2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = if (index == 0) 12.dp else 0.dp,
                                        bottomEnd = if (index == 2) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(displayedImages[imageIndex], imageIndex) }
                        ) {
                            AsyncImage(
                                model = displayedImages[imageIndex],
                                contentDescription = "Post image ${imageIndex + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )

                            // Overlay for the last image showing extra count
                            if (index == 2 && extraImageCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "+$extraImageCount",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "more",
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ImageDetailDialog(
    imageUrls: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { imageUrls.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        currentIndex = pagerState.currentPage
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Image counter
            Text(
                text = "${currentIndex + 1} / ${imageUrls.size}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .zIndex(1f)
            )

            // Image pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrls[page],
                        contentDescription = "Image ${page + 1}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { /* Prevent dismiss when clicking image */ }
                    )
                }
            }

            // Page indicator dots (only show if more than 1 image)
            if (imageUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(imageUrls.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentIndex) Color.White
                                    else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }
        }
    }
}