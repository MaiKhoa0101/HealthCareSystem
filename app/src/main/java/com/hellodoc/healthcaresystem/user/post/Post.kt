package com.hellodoc.healthcaresystem.user.post

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.responsemodel.MediaType
import com.hellodoc.healthcaresystem.user.home.confirm.ConfirmDeletePostModal
import com.hellodoc.healthcaresystem.user.home.report.ReportPostUser
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


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
            var showPostDeleteConfirmDialog by remember { mutableStateOf(false) }

            Box (modifier = Modifier.fillMaxWidth()) {
                Post(
                    navHostController = navHostController,
                    postViewModel = postViewModel,
                    post = post,
                    userWhoInteractWithThisPost = userWhoInteractWithThisPost,
                    onClickReport = {
//                        showOptionsMenu = true
                        showPostReportDialog = !showPostReportDialog
                    },
                    onClickDelete = {
//                        showOptionsMenu = true
                        showPostDeleteConfirmDialog = !showPostDeleteConfirmDialog
                    },
                )

                if (showPostReportDialog) {
                    post.user?.let {
                        ReportPostUser(
                            context = navHostController.context,
                            youTheCurrentUserUseThisApp = userWhoInteractWithThisPost,
                            userReported = it,
                            onClickShowPostReportDialog = { showPostReportDialog = false },
                            sharedPreferences = navHostController.context.getSharedPreferences(
                                "MyPrefs",
                                Context.MODE_PRIVATE
                            )
                        )
                    }
                }

                if (showPostDeleteConfirmDialog) {
                    ConfirmDeletePostModal(
                        postId = post.id,
                        postViewModel = postViewModel,
                        sharedPreferences = navHostController.context.getSharedPreferences(
                            "MyPrefs",
                            Context.MODE_PRIVATE
                        ),
                        onClickShowConfirmDeleteDialog = { showPostDeleteConfirmDialog = false },
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
    onClickReport: () -> Unit,
    onClickDelete: () -> Unit
) {

    HorizontalDivider(thickness = 2.dp)
    Box (
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            PostHeader(
                navHostController = navHostController,
                userWhoInteractWithThisPost,
                post,
                onClickReport,
                onClickDelete
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostBody(post)
            Spacer(modifier = Modifier.height(8.dp))
            PostMedia(
                post = post,
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

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mediaItem = androidx.media3.common.MediaItem.fromUri(videoUrl)
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = modifier
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(isoString: String): String {
    val zonedDateTime = ZonedDateTime.parse(isoString)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return zonedDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostHeader(
    navHostController: NavHostController,
    userWhoInteractWithThisPost: User,
    post: PostResponse,
    onClickReport: () -> Unit,
    onClickDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                // Avatar + tên
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = post.user?.avatarURL,
                        contentDescription = "Avatar of ${post.user?.name}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (post.user?.id != userWhoInteractWithThisPost.id) {
                                    navHostController.navigate("otherUserProfile/${post.user?.id}")
                                } else {
                                    navHostController.navigate("personal")
                                }
                            },
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.clickable {
                            if (post.user?.id != userWhoInteractWithThisPost.id) {
                                navHostController.navigate("otherUserProfile/${post.user?.id}")
                            } else {
                                navHostController.navigate("personal")
                            }
                        }
                    ) {
                        Text(
                            text = post.user?.name ?: "Người dùng ẩn",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatDateTime(post.createdAt),
                            fontSize = 12.sp
                        )
                    }
                }

            }

            // Nút 3 chấm và Dropdown gắn liền
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = DpOffset(x = (-110).dp, y = (-40).dp)
                ) {
                    if (userWhoInteractWithThisPost.id == post.user?.id) {
                        // Người đăng bài == người hiện tại → chỉ cho xoá
                        DropdownMenuItem(
                            text = { Text("Chỉnh sửa bài viết") },
                            onClick = {
                                showMenu = false
                                navHostController.navigate("edit_post/${post.id}")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Xoá bài viết") },
                            onClick = {
                                showMenu = false
                                onClickDelete()
                            }
                        )
                    } else {
                        // Người khác → chỉ cho báo cáo
                        DropdownMenuItem(
                            text = { Text("Báo cáo bài viết") },
                            onClick = {
                                showMenu = false
                                onClickReport()
                            }
                        )
                    }
                }

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

) {
    var showMediaDetail by remember { mutableStateOf(false) }
    var selectedMediaIndex by remember { mutableStateOf(0) }

    if (post.media.isNotEmpty()) {
        MediaGrid(
            mediaUrls = post.media,
            onMediaClick = { url, index ->
                selectedMediaIndex = index
                showMediaDetail = true
            }
        )
        Spacer(modifier = Modifier.height(6.dp))
    }

    if (showMediaDetail && post.media.isNotEmpty()) {
        MediaDetailDialog(
            mediaUrls = post.media,
            initialIndex = selectedMediaIndex,
            onDismiss = { showMediaDetail = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGrid(
    mediaUrls: List<String>,
    modifier: Modifier = Modifier,
    onMediaClick: ((String, Int) -> Unit)? = null
) {
    val maxImagesToShow = 5
    val extraImageCount = mediaUrls.size - maxImagesToShow

    if (mediaUrls.isEmpty()) {
        return
    }
    when (mediaUrls.size) {
        1 -> {
            // Single media - full width with proper aspect ratio
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onMediaClick?.invoke( mediaUrls[0], 0) }
            ) {
                SingleMediaItem(
                    url = mediaUrls[0],
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .shadow(10.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        }

        2 -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                mediaUrls.forEachIndexed { index, url ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(if (index == 0) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 0) 12.dp else 0.dp))
                            .clickable { onMediaClick?.invoke( url , index) }
                    ) {
                        SingleMediaItem(
                            url = url,
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(if (index == 0) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 0) 12.dp else 0.dp))
                        )
                    }
                }
            }
        }

        3 -> {
            Row(
                modifier = modifier.fillMaxWidth().height(300.dp).shadow(5.dp, RoundedCornerShape(12.dp)),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { onMediaClick?.invoke(mediaUrls[0], 0) }
                ) {
                    SingleMediaItem(
                        url = mediaUrls[0],
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    mediaUrls.slice(1..2) .forEachIndexed { index, url ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onMediaClick?.invoke( url , index) }
                        ) {
                            SingleMediaItem(
                                url = url,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(0.dp, if (index == 0) 12.dp else 0.dp, if (index == 0) 0.dp else 12.dp, 0.dp))
                            )
                        }
                    }
                }
            }
        }

        4 -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        SingleMediaItem(
                            url = mediaUrls[index],
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 12.dp else 0.dp,
                                        topEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(2) { index ->
                        val imageIndex = index + 2
                        SingleMediaItem(
                            url = mediaUrls[imageIndex],
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = if (index == 0) 12.dp else 0.dp,
                                        bottomEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                        )
                    }
                }
            }
        }

        else -> {
            val displayedMedia = mediaUrls.take(maxImagesToShow)
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(2) { index ->
                        SingleMediaItem(
                            url = displayedMedia[index],
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 12.dp else 0.dp,
                                        topEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { index ->
                        val mediaIndex = index + 2
                        SingleMediaItem(
                            url = displayedMedia[mediaIndex],
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = if (index == 0) 12.dp else 0.dp,
                                        bottomEnd = if (index == 2) 12.dp else 0.dp
                                    )
                                ),
                            overlayText = if (index == 2 && extraImageCount > 0) "+$extraImageCount\nmore" else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleMediaItem(
    url: String,
    modifier: Modifier,
    overlayText: String? = null
) {
    Box(
        modifier = modifier.background(Color.Gray.copy(alpha = 0.1f))
    ) {
        when (detectMediaType(url)) {
            MediaType.IMAGE -> {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f))
                )
            }
            MediaType.VIDEO -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = url, // This might be a video thumbnail URL
                        contentDescription = "Video thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Play button overlay
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                }
            }
            MediaType.UNKNOWN -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Unknown", color = Color.White)
                }
            }
        }

        if (overlayText != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = overlayText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun MediaDetailDialog(
    mediaUrls: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { mediaUrls.size }
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
                .background(MaterialTheme.colorScheme.onBackground)
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
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Image counter
            Text(
                text = "${currentIndex + 1} / ${mediaUrls.size}",
                color = MaterialTheme.colorScheme.background,
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
                    when (detectMediaType(mediaUrls[page])) {
                        MediaType.IMAGE -> {
                            AsyncImage(
                                model = mediaUrls[page],
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
                        MediaType.VIDEO ->{
                            VideoPlayer(
                                videoUrl = mediaUrls[page],
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { /* Prevent dismiss when clicking image */ }
                            )
                        }
                        else -> {
                            Text("Unknown media type")
                        }
                    }
                }
            }

            // Page indicator dots (only show if more than 1 image)
            if (mediaUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(mediaUrls.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentIndex) MaterialTheme.colorScheme.background
                                    else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }
        }
    }
}

fun detectMediaType(url: String): MediaType {
    val lowerUrl = url.lowercase()
    return when {
        lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") || lowerUrl.endsWith(".png") || lowerUrl.endsWith(
            ".webp"
        ) ||
                "images" in lowerUrl -> MediaType.IMAGE

        lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mov") || lowerUrl.endsWith(".avi") ||
                "videos" in lowerUrl -> MediaType.VIDEO

        else -> MediaType.UNKNOWN
    }
}


