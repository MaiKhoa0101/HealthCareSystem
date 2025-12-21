package com.hellodoc.healthcaresystem.blindview.userblind.post

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.core.common.skeletonloading.SkeletonBox
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.MediaType
import com.hellodoc.healthcaresystem.view.user.home.confirm.ConfirmDeletePostModal
import com.hellodoc.healthcaresystem.view.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.view.user.post.PostHeader
import com.hellodoc.healthcaresystem.view.user.post.PostMedia
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.VideoPlayer
import com.hellodoc.healthcaresystem.view.user.supportfunction.speakQueue
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Post(
    navHostController:NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    userWhoInteractWithThisPost: User,
    onClickReport: () -> Unit,
    onClickDelete: () -> Unit,
) {

    // Lấy trạng thái favorite từ ViewModel
    LaunchedEffect(post.id, userWhoInteractWithThisPost.id) {
        postViewModel.fetchFavoriteForPost(
            postId = post.id,
            userId = userWhoInteractWithThisPost.id
        )
    }

    // Thêm các thao tác chạm/nhấn giữ cho người khiếm thị
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize() // Sử dụng fillMaxSize() bên trong fillParentMaxSize() container
            .padding(3.dp)
            .shadow(1.dp, RoundedCornerShape(3.dp,), clip = true, spotColor = Color.Gray)
            .clip(RoundedCornerShape(10.dp))
            .padding(2.dp)
            .border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(3.dp))

            .padding(horizontal = 15.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
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
            // Dùng Spacer với weight để đẩy nội dung lên trên cùng và chiếm hết không gian còn lại.
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(post.id) {
                    detectTapGestures(
                        // Chạm đơn: Đọc lại bài viết
                        onTap = {
                            println("Đã nhận ra cham")
                            SoundManager.playTap()
                            vibrate(context)
                            scope.launch {
                                val postText = post.content?.takeIf { it.isNotBlank() }
                                    ?: "Bài viết này không có nội dung văn bản."
                                speakQueue("Nội dung bài viết: ${postText}.")
                            }
                        },
                        // Chạm đúp: Chuyển đến chi tiết bài viết
                        onDoubleTap = {
                            SoundManager.playTap()
                            vibrate(context, 100)
                            navHostController.navigate("post-detail/${post.id}") {
                                restoreState = true
                            }
                        },
                        // Nhấn giữ: Mở menu/tùy chọn (tương tự Tutorial 4)
                        onLongPress = {
                            SoundManager.playHold()
                            vibrate(context, 60)
                            scope.launch {
                                speakQueue("Menu tùy chọn bài viết đã được mở.")
                                // Logic mở menu thực tế (nếu có) sẽ nằm ở đây
                            }
                        }
                    )
                }
        ) { }

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
