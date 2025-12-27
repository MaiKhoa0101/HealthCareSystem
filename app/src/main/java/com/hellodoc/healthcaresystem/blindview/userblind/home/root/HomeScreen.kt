package com.hellodoc.healthcaresystem.blindview.userblind.home.root

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hellodoc.core.common.skeletonloading.SkeletonBox
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetMedicalOptionResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UiState
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.blindview.userblind.home.confirm.ConfirmDeletePostModal

import com.hellodoc.healthcaresystem.blindview.userblind.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.blindview.userblind.post.Post
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.speakQueue
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthMateHomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var currentPostIndex by rememberSaveable { mutableIntStateOf(0) }

    val doctorViewModel: DoctorViewModel = hiltViewModel()
    val specialtyViewModel: SpecialtyViewModel = hiltViewModel()
    val medicalOptionViewModel: MedicalOptionViewModel = hiltViewModel()
    val geminiViewModel: GeminiViewModel = hiltViewModel()
    val postViewModel: PostViewModel = hiltViewModel()
    val newsViewModel: NewsViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val faqItemViewModel: FAQItemViewModel = hiltViewModel()

    val user by userViewModel.user.collectAsState()
    val hasMorePosts by postViewModel.hasMorePosts.collectAsState()
    val isLoadingMorePosts by postViewModel.isLoadingMorePosts.collectAsState()
    val posts by postViewModel.posts.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var showReportBox by remember { mutableStateOf(false) }
    var userModel by remember { mutableStateOf("") }
    var doneListening by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val username = userViewModel.getUserAttribute("name", context)
        userModel = userViewModel.getUserAttribute("role", context)

        userViewModel.getUser(userViewModel.getUserAttribute("userId", context))
        postViewModel.fetchPosts()

        doctorViewModel.fetchDoctors()
        medicalOptionViewModel.fetchMedicalOptions()
        newsViewModel.getAllNews()
        delay(2000)

        FocusTTS.speakAndWait("Bạn đang trong trang chủ chứa các bài viết")
        FocusTTS.speakAndWait("Chạm để nghe bài viết hiện tại")
        FocusTTS.speakAndWait("Trượt lên để xem bài viết mới hơn, trượt xuống để quay lại bài viết cũ hơn.")
        FocusTTS.speakAndWait("Trượt sang phải để chuyển sang trang đặt lịch khám")
        FocusTTS.speakAndWait("Ấn giữ màn hình để nghe lại hướng dẫn và hỗ trợ")
        delay(2000)
        doneListening = true
    }

    LaunchedEffect(currentPostIndex, hasMorePosts, isLoadingMorePosts) {
        snapshotFlow {
            val threshold = posts.size - 2
            currentPostIndex >= threshold
        }.distinctUntilChanged().collect { isNearEnd ->
            if (isNearEnd && hasMorePosts && !isLoadingMorePosts) {
                println("Load more với skip=${posts.size}")
                postViewModel.fetchPosts(
                    skip = posts.size,
                    limit = 10,
                    append = true
                )
            }
        }
    }

    LaunchedEffect(currentPostIndex, posts.size) {
        if (posts.isNotEmpty() && currentPostIndex >= 0 && currentPostIndex < posts.size) {
            coroutineScope.launch {
                listState.animateScrollToItem(currentPostIndex)

                val post = posts[currentPostIndex]
                val postContent = post.content?.takeIf { it.isNotBlank() } ?: "Bài viết này không có nội dung văn bản."

                val mediaInfo = if (post.media.isNotEmpty()) {
                    val mediaType = post.media.first()
                    if (mediaType.startsWith("image")) "Bài viết này có kèm ảnh minh họa."
                    else if (mediaType.startsWith("video")) "Bài viết này có kèm video."
                    else ""
                } else ""
            }
        }
    }

    LaunchedEffect(navHostController.currentBackStackEntry) {
        if (navHostController.currentBackStackEntry?.destination?.route == "home") {
            if (postViewModel.posts.value == emptyList<PostResponse>()) {
                currentPostIndex = 0
                postViewModel.clearPosts()
                postViewModel.fetchPosts()
            }
        }
    }

    if (doneListening) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        postViewModel.closeAllPostMenus()
                        showReportBox = false
                    }
                }
        ) {
            var offsetY by remember { mutableFloatStateOf(0f) }
            var offsetX by remember { mutableFloatStateOf(0f) }
            val dragThreshold = 100f
            val horizontalDragThreshold = 150f // Ngưỡng cho vuốt ngang

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(posts.size, isLoadingMorePosts, currentPostIndex) {
                        detectDragGestures(
                            onDragEnd = {
                                offsetY = 0f
                                offsetX = 0f
                            }
                        ) { change, dragAmount ->
                            val dragX = dragAmount.x
                            val dragY = dragAmount.y

                            // Kiểm tra hướng vuốt chính (ngang hay dọc)
                            if (kotlin.math.abs(dragX) > kotlin.math.abs(dragY)) {
                                // Vuốt ngang
                                offsetX += dragX

                                if (offsetX > horizontalDragThreshold) {
                                    SoundManager.playSwipe()
                                    vibrate(context)
                                    navHostController.navigate("booking_blind")
                                    offsetX = 0f
                                }
                            } else {
                                // Vuốt dọc
                                offsetY += dragY

                                // Swipe UP -> Bài mới hơn
                                if (offsetY < -dragThreshold) {
                                    val nextIndex = currentPostIndex + 1
                                    if (nextIndex < posts.size) {
                                        SoundManager.playSwipe()
                                        vibrate(context)
                                        currentPostIndex = nextIndex
                                        offsetY = 0f
                                        coroutineScope.launch {
                                            FocusTTS.speakAndWait("Đã chuyển đến bài viết mới.")
                                        }
                                    } else if (hasMorePosts && !isLoadingMorePosts) {
                                        coroutineScope.launch {
                                            FocusTTS.speakAndWait("Đang tải thêm bài viết mới.")
                                        }
                                        offsetY = 0f
                                    } else if (!hasMorePosts) {
                                        coroutineScope.launch {
                                            FocusTTS.speakAndWait("Bạn đã xem hết tất cả bài viết.")
                                        }
                                        offsetY = 0f
                                    }
                                }

                                // Swipe DOWN -> Bài cũ hơn
                                if (offsetY > dragThreshold) {
                                    val previousIndex = currentPostIndex - 1
                                    if (previousIndex >= 0) {
                                        SoundManager.playSwipe()
                                        vibrate(context)
                                        currentPostIndex = previousIndex
                                        offsetY = 0f
                                        coroutineScope.launch {
                                            FocusTTS.speakAndWait("Đã quay lại bài viết trước đó")
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            FocusTTS.speakAndWait("Bạn đã ở bài viết đầu tiên.")
                                        }
                                        offsetY = 0f
                                    }
                                }
                            }
                        }
                    },
                state = listState,
                userScrollEnabled = false
            ) {
                items(posts) { post ->
                    var showPostReportDialog by remember { mutableStateOf(false) }
                    var showPostDeleteConfirmDialog by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillParentMaxSize()) {
                        Post(
                            navHostController = navHostController,
                            postViewModel = postViewModel,
                            post = post,
                            userWhoInteractWithThisPost = user!!,
                            onClickReport = {
                                showPostReportDialog = !showPostReportDialog
                            },
                            onClickDelete = {
                                showPostDeleteConfirmDialog = !showPostDeleteConfirmDialog
                            },
                        )

                        if (showPostReportDialog) {
                            post.userInfo?.let {
                                ReportPostUser(
                                    context = navHostController.context,
                                    youTheCurrentUserUseThisApp = user!!,
                                    userReported = it,
                                    postId = post.id,
                                    onClickShowPostReportDialog = { showPostReportDialog = false }
                                )
                            }
                        }

                        if (showPostDeleteConfirmDialog) {
                            ConfirmDeletePostModal(
                                postId = post.id,
                                postViewModel = postViewModel,
                                sharedPreferences = navHostController.context.getSharedPreferences(
                                    "user_prefs",
                                    Context.MODE_PRIVATE
                                ),
                                onClickShowConfirmDeleteDialog = {
                                    showPostDeleteConfirmDialog = false
                                },
                            )
                        }
                    }
                }
            }

        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Đang hướng dẫn...")
        }
    }
}



@Composable
fun BackToTopButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animation shimmer
    val transition = rememberInfiniteTransition(label = "shimmer")
    // Animation chạy dọc trục Y (từ dưới lên trên)
    val translateAnim = transition.animateFloat(
        initialValue = 400f,   // bắt đầu ở dưới
        targetValue = -400f,   // chạy ngược lên trên
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            Color.Transparent
        ),
        start = Offset(x = 0f, y = translateAnim.value + 200f), // dưới
        end   = Offset(x = 0f, y = translateAnim.value)         // trên
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .background(shimmerBrush)   // shimmer overlay
            .clickable { onClick() },   // thay vì FloatingActionButton
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardDoubleArrowUp,
            contentDescription = "Back to Top",
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}





//lặp lại title 3 lần nếu độ dài < N ký tự
fun forceMarqueeText(title: String, repeatCount: Int = 3): String {
    return if (title.length < 30) {
        (title + "     ").repeat(repeatCount)
    } else {
        title
    }
}

fun calculateDynamicDelay(title: String, velocityDpPerSecond: Float = 50f, screenWidthDp: Float = 360f): Long {
    val estimatedTextWidthDp = title.length * 8f  //giả sử mỗi ký tự khoảng 8dp (với font 16sp)
    val scrollDistanceDp = maxOf(estimatedTextWidthDp, screenWidthDp)// Nếu text ngắn hơn screen → ép delay ngắn tối thiểu
    val scrollTimeSec = scrollDistanceDp / velocityDpPerSecond
    return (scrollTimeSec * 1000).toLong() + 2000L  //thêm 2s dừng nhẹ giữa các title
}

@Composable
fun MarqueeNewsTicker(
    user: User?,
    newsList: List<NewsResponse>,
    navHostController: NavHostController
) {
    var showAllNews by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!showAllNews) {
            val firstHalf = newsList.take(newsList.size / 2)
            val secondHalf = newsList.drop(newsList.size / 2)

            var firstIndex by remember { mutableStateOf(0) }
            var secondIndex by remember { mutableStateOf(0) }

            LaunchedEffect(firstIndex, firstHalf) {
                if (firstHalf.isNotEmpty()) {
                    while (!showAllNews) {
                        val currentTitle = forceMarqueeText(firstHalf.getOrNull(firstIndex)?.title.orEmpty())
                        val delayTime = calculateDynamicDelay(currentTitle)
                        delay(delayTime)
                        firstIndex = (firstIndex + 1) % firstHalf.size
                    }
                }
            }

            LaunchedEffect(secondIndex, secondHalf) {
                if (secondHalf.isNotEmpty()) {
                    while (!showAllNews) {
                        val currentTitle = forceMarqueeText(secondHalf.getOrNull(secondIndex)?.title.orEmpty())
                        val delayTime = calculateDynamicDelay(currentTitle)
                        delay(delayTime)
                        secondIndex = (secondIndex + 1) % secondHalf.size
                    }
                }
            }

            // Nội dung marquee
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = forceMarqueeText(firstHalf.getOrNull(firstIndex)?.title.orEmpty()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            firebaseAnalytics.logEvent("reading_news", bundleOf(
                                "Id_user" to user?.id,
                                "name_user" to user?.name,
                                "reading_new_id" to firstHalf[firstIndex].id
                            ))
                            firstHalf.getOrNull(firstIndex)?.let { news ->
                                navHostController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                                navHostController.navigate("news_detail")
                            }
                        }
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            animationMode = MarqueeAnimationMode.Immediately,
                            spacing = MarqueeSpacing(50.dp),
                            velocity = 50.dp,
                        ),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = forceMarqueeText(secondHalf.getOrNull(secondIndex)?.title.orEmpty()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            firebaseAnalytics.logEvent("reading_news", bundleOf(
                                "Id_user" to user?.id,
                                "name_user" to user?.name,
                                "reading_new_id" to secondHalf[secondIndex].id
                            ))
                            secondHalf.getOrNull(secondIndex)?.let { news ->
                                navHostController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                                navHostController.navigate("news_detail")
                            }
                        }
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            animationMode = MarqueeAnimationMode.Immediately,
                            spacing = MarqueeSpacing(50.dp),
                            velocity = 50.dp,
                        ),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
            }
        } else {
            NewsItemList(newsList = newsList, navHostController = navHostController)
        }

        // Nút xem thêm luôn nằm dưới cùng, tách riêng
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = if (showAllNews) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showAllNews) "Thu gọn" else "Xem thêm",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .clickable { showAllNews = !showAllNews }
                    .size(30.dp)
            )
        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun EmptyList(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không có $name",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AssistantAnswerDialog(
    question: String,
    answer: String,
    onDismiss: () -> Unit
) {
    val displayAnswer = answer.ifBlank { "Đang xử lý..." }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Trợ lý AI", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(min = 100.dp, max = 300.dp)) {
                item {
                    Text("Q: $question", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A: $displayAnswer")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun AssistantQueryRow(
    navHostController: NavHostController,
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.9f)) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Đặt câu hỏi cho Trợ lý AI", color = MaterialTheme.colorScheme.onBackground) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.DoubleArrow,
            contentDescription = "submit question for AI",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    if (text.isNotBlank()) {
                        navHostController.currentBackStackEntry?.savedStateHandle?.set("first_question", text)
                        text = ""
                        navHostController.navigate("gemini_help")
                    }
                },
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun NewsSkeletonItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .padding(end = 32.dp),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = MaterialTheme.colorScheme.secondaryContainer, thickness = 1.dp)
    }
}

@Composable
fun NewsSkeletonList(count: Int = 2) {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(count) {
            NewsSkeletonItem()
        }
    }
}


@Composable
fun NewsItem(
    news: NewsResponse,
    onSelectNews: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectNews() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = news.title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = MaterialTheme.colorScheme.onPrimaryContainer, thickness = 1.dp)
    }
}
@Composable
fun NewsItemList(
    newsList: List<NewsResponse>,
    navHostController: NavHostController
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        newsList.forEach { news ->
            Log.d("NewsDebug", "News in list: ${news.title}, id: ${news.id}")
            NewsItem(news = news,
                onSelectNews = {
                navHostController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                    Log.d("NewsSet", "News set to savedStateHandle: ${news.id}")
                navHostController.navigate("news_detail")
            }
            )
        }
    }
}

@Composable
fun ServiceSkeletonGrid() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        repeat(1) { // 1 hàng, mỗi hàng 1 ô
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(1) {
                    SkeletonBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun GridServiceList(items: List<GetMedicalOptionResponse>, onClick: (GetMedicalOptionResponse) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { item ->
                    key(item.id) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onClick(item) }
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp)

                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id =
                                        if (item.name == "Tính BMI") {
                                            R.drawable.doctor
                                        }
                                        else {
                                            R.drawable.speak
                                        }
                                    ),
                                    contentDescription = item.name,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = item.name, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SpecialtySkeletonList() {
    Column {
        // Header skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp),
                shape = RoundedCornerShape(4.dp)
            )

            SkeletonBox(
                modifier = Modifier
                    .width(60.dp)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            items(6) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SkeletonBox(
                            modifier = Modifier
                                .size(80.dp)
                                .height(80.dp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SpecialtyList(
    navHostController: NavHostController,
    context: Context,
    specialties: List<GetSpecialtyResponse>
) {
    var showAllSpecialties by remember { mutableStateOf(false) }
    val displayedSpecialties = if (showAllSpecialties) specialties else specialties.take(6)

    Column {
        // Header row with title and "See more" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chuyên khoa",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            if (specialties.size > 6) {
                Text(
                    text = if (showAllSpecialties) "Thu gọn" else "Xem thêm",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showAllSpecialties = !showAllSpecialties }
                        .padding(start = 8.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.width(8.dp)) }
            items(displayedSpecialties,  key = { specialty -> "specialty_${specialty.id}" }) { specialty ->
                SpecialtyItem(
                    navHostController = navHostController,
                    specialty = specialty,
                    onClick = { showToast(context, "Đã chọn: ${specialty.name}") }
                )
            }
            item { Spacer(modifier = Modifier.width(8.dp)) }
        }
    }
}

@Composable
fun SpecialtyItem(
    navHostController: NavHostController,
    specialty: GetSpecialtyResponse,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp))
            .clickable {
                firebaseAnalytics.logEvent("specialty_selected", bundleOf(
                    "ID_specialty" to specialty.id,
                    "Name_of_specialty" to specialty.name,
                ))
                onClick()
                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("specialtyId", specialty.id)
                }
                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("specialtyName", specialty.name)
                }
                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("specialtyDesc", specialty.description)
                }
                navHostController.navigate("doctor_list")
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!specialty.icon.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(specialty.icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = specialty.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = specialty.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = specialty.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(), // Đảm bảo chiếm hết chiều rộng để căn giữa chính xác
                maxLines = 2,
                overflow = TextOverflow.Ellipsis, // Thêm dấu "..." nếu quá 2 dòng
                softWrap = true // Cho phép xuống dòng mềm
            )
        }
    }
}

@Composable
fun DoctorSkeletonList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer) // cùng tone với phần real content
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp),
                shape = RoundedCornerShape(4.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .width(60.dp)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(6) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(120.dp)
                ) {
                    // Avatar circle
                    SkeletonBox(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        shape = CircleShape
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Name line
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp),
                        shape = RoundedCornerShape(4.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Specialty line
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorList(
    navHostController: NavHostController,
    doctors: List<GetDoctorResponse>
) {
    var showAllDoctors by remember { mutableStateOf(false) }
    val displayedDoctors = if (showAllDoctors) doctors else doctors.take(6)

    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.tertiaryContainer)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bác sĩ nổi bật",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (doctors.size > 6) {
                Text(
                    text = if (showAllDoctors) "Thu gọn" else "Xem thêm",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showAllDoctors = !showAllDoctors }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(displayedDoctors,  key = { doctor -> "doctor_${doctor.id}" }) { doctor ->
                DoctorItem(doctor) {
                    firebaseAnalytics.logEvent("doctor_selected", bundleOf(
                        "doctor_id" to doctor.id,
                        "doctor_name" to doctor.name,
                    ))
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("doctorId", doctor.id)
                    }
                    navHostController.navigate("other_user_profile")
                }
            }
        }
    }
    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.tertiaryContainer)
}

@Composable
fun DoctorItem(doctor: GetDoctorResponse, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!doctor.avatarURL.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(doctor.avatarURL)
                    .crossfade(true)
                    .build(),
                contentDescription = doctor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = doctor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = doctor.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = doctor.specialty.name,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun UserPostSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
    ) {
        // Header: Avatar + Name
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape),
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }

        // Nội dung bài viết (text)
        Spacer(modifier = Modifier.height(16.dp))
        repeat(2) {
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }

        // Ảnh bài viết
        Spacer(modifier = Modifier.height(10.dp))
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .height(250.dp),
            shape = RoundedCornerShape(10.dp)
        )

        // Like - Comment buttons
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp),
                shape = RoundedCornerShape(6.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp),
                shape = RoundedCornerShape(6.dp)
            )
        }
    }

}
