package com.hellodoc.healthcaresystem.view.user.home.root

import android.content.Context
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UiState
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.view.model_human.Floating3DAssistant
import com.hellodoc.healthcaresystem.view.user.home.confirm.ConfirmDeletePostModal

import com.hellodoc.healthcaresystem.view.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.view.user.post.Post
import com.hellodoc.healthcaresystem.viewmodel.*
import io.github.sceneview.environment.Environment
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.random.Random
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer

// HelloDoc Brand Colors
val HelloDocYellow = Color(0xFFFFD846)
val HelloDocDark = Color(0xFF2C3E50)
val HelloDocAccent = Color(0xFFFF9F43) // A warm orange-yellow for accents
val HelloDocSoftYellow = Color(0xFFFFF9DB)



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthMateHomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val listState = rememberSaveable(
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }
    val coroutineScope = rememberCoroutineScope()

    val isScrollButtonVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 3 //hien thi khi scroll đến vị trí thứ 3
        }
    }


    val doctorViewModel: DoctorViewModel = hiltViewModel()
    val specialtyViewModel: SpecialtyViewModel = hiltViewModel()
    val geminiViewModel: GeminiViewModel = hiltViewModel()
    val postViewModel: PostViewModel = hiltViewModel()
    val newsViewModel: NewsViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val faqItemViewModel: FAQItemViewModel = hiltViewModel()

    // Collect states with loading information
    val doctorState by doctorViewModel.doctors.collectAsState()
    val specialtyState by specialtyViewModel.specialties.collectAsState()
    val medicalOptions = listOf("Tính BMI", "Fast Talk")
    val question by geminiViewModel.question.collectAsState()
    val answer by geminiViewModel.answer.collectAsState()
    val newsState by newsViewModel.newsList.collectAsState()
    val user by userViewModel.user.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var showReportBox by remember { mutableStateOf(false) }
    var postIndex by remember { mutableStateOf(0) }
    var userModel by remember { mutableStateOf("") }
    var username = ""
    // --- KHỞI TẠO ENGINE 3D Ở CẤP CAO NHẤT ---
    // Engine sẽ sống cùng vòng đời của HomeScreen -> Không bao giờ bị kill bất tử
//    val engine = rememberEngine()
//    val modelLoader = rememberModelLoader(engine)
//    val environmentLoader = rememberEnvironmentLoader(engine) // Khởi tạo Loader
// --- 2. BIẾN LƯU TRỮ TÀI NGUYÊN TOÀN CỤC ---
//    var ericModelInstance by remember { mutableStateOf<ModelInstance?>(null) }
//    var globalEnvironment by remember { mutableStateOf<Environment?>(null) }
    LaunchedEffect(Unit) {
        username = userViewModel.getUserAttribute("name", context)
        userModel = userViewModel.getUserAttribute("role", context)

        userViewModel.getUser(userViewModel.getUserAttribute("userId", context))
        postViewModel.fetchPosts()
        postIndex=10
        println("Gọi 1 voi index: "+ postIndex)

        doctorViewModel.fetchDoctors()
        specialtyViewModel.fetchSpecialties()
        newsViewModel.getAllNews()
//        if (ericModelInstance == null) {
//            try {
//                val inputStream = context.assets.open("BoneEric.glb")
//                val bytes = inputStream.readBytes()
//                inputStream.close()
//                val buffer = ByteBuffer.wrap(bytes)
////                ericModelInstance = modelLoader.createModelInstance(buffer)
//                println("Lay hinh thanh cong")
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        // B. Nạp Môi trường (HDR)
//        if (globalEnvironment == null) {
//            try {
//                // Lưu ý: Đảm bảo file environment.hdr < 10MB để tránh OOM
//                globalEnvironment = environmentLoader.createHDREnvironment(
//                    assetFileLocation = "environment.hdr"
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    val hasMorePosts by postViewModel.hasMorePosts.collectAsState()
    val isLoadingMorePosts by postViewModel.isLoadingMorePosts.collectAsState()

    val progress by postViewModel.uploadProgress.collectAsState()
    val uiStatePost by postViewModel.uiStatePost.collectAsState()
    val posts by postViewModel.posts.collectAsState()

    LaunchedEffect(listState, hasMorePosts, isLoadingMorePosts) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = layoutInfo.totalItemsCount
            lastVisible >= total - 2
        }.distinctUntilChanged().collect { isAtEnd ->
            if (isAtEnd && hasMorePosts && !isLoadingMorePosts) {
                println("Load more với skip=${posts.size}")
                postViewModel.fetchPosts(
                    skip = posts.size,
                    limit = 10,
                    append = true
                )
            }
        }
    }

    LaunchedEffect(navHostController.currentBackStackEntry) {
        if (navHostController.currentBackStackEntry?.destination?.route == "home") {
            if (postViewModel.posts == emptyList<PostResponse>()) {
                postIndex = 0
                postViewModel.clearPosts()
                postViewModel.fetchPosts()
            }
        }
    }




    // State quản lý trạng thái mở rộng của nút 3D
    var is3DExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Bắt sự kiện chạm vào vùng trống để tắt 3D
            .pointerInput(Unit) {
                detectTapGestures {
                    postViewModel.closeAllPostMenus()
                    showReportBox = false

                    // Logic tắt 3D khi bấm ra ngoài
                    if (is3DExpanded) {
                        is3DExpanded = false
                    }
                }
            }
    ) {
        // HelloDoc Welcome atmosphere

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = listState
        ) {

            item(key = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    HelloDocYellow.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    // Premium HelloDoc Welcome Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .shadow(12.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        HelloDocDark,
                                        Color(0xFF34495E),
                                        HelloDocDark
                                    )
                                )
                            )
                            .border(2.dp, HelloDocYellow.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Welcome to HelloDoc",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = HelloDocYellow.copy(alpha = 0.8f),
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SỨC KHỎE LÀ VÀNG",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HelloDocYellow,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.height(1.dp).width(30.dp).background(HelloDocYellow))
                                Text(
                                    text = " 2026 ",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HelloDocYellow
                                    )
                                )
                                Box(modifier = Modifier.height(1.dp).width(30.dp).background(HelloDocYellow))
                            }
                        }
                    }
                    // Header with Greeting and Profile
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Chào mừng quay lại, 👋",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = user?.name ?: "Người dùng",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }

                        // Profile Icon
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(HelloDocYellow)
                                .border(2.dp, HelloDocYellow, CircleShape)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (!user?.avatarURL.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(user?.avatarURL)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "User Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.doctor), // Fallback to a default icon
                                    contentDescription = "User Avatar",
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // AI Search Bar
                    AssistantQueryRow(
                        navHostController = navHostController,
                        onSubmit = { query ->
                            geminiViewModel.processUserQuery(query)
                            showDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (newsState.isEmpty()) {
                        NewsSkeletonList()
                    } else {
                        MarqueeNewsTicker(user = user, newsList = newsState, navHostController = navHostController)
                    }
                }
            }

            item(key = "services") {
                SectionHeader(title = "Dịch vụ toàn diện")
                GridServiceList(medicalOptions) { optionName ->
                    when (optionName) {
                        "Tính BMI" -> navHostController.navigate("bmi-checking")
                        "Fast Talk" -> navHostController.navigate("fast_talk")
                        else -> {

                        }
                    }
                }
            }

            item(key = "specialties") {
                if (specialtyState.isEmpty()) {
//                    EmptyList("chuyên khoa")
                    SpecialtySkeletonList()
                } else {
                    SpecialtyList(
                        navHostController = navHostController,
                        context = context,
                        specialties = specialtyState)
                }
            }

            item(key = "doctors") {
                Spacer(modifier = Modifier.height(8.dp))
                if (doctorState.isEmpty()) {
//                    EmptyList("bác sĩ")
                    DoctorSkeletonList()
                } else {
                    println("ko co bi empty")
                    DoctorList(navHostController = navHostController, doctors = doctorState)
                }
            }

            items(posts) { post ->
                var showPostReportDialog by remember { mutableStateOf(false) }
                var showPostDeleteConfirmDialog by remember { mutableStateOf(false) }
                Box (modifier = Modifier.fillMaxWidth()) {
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
                                "MyPrefs",
                                Context.MODE_PRIVATE
                            ),
                            onClickShowConfirmDeleteDialog = { showPostDeleteConfirmDialog = false },
                        )
                    }
                }
            }

            item {
                if (isLoadingMorePosts) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (!hasMorePosts) {
                    Text(
                        text = "Đã hết bài viết",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }


            item(key = "bottom_space") {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (isScrollButtonVisible) {
            BackToTopButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },

            )
        }

        if (showDialog) {
            AssistantAnswerDialog(
                question = question,
                answer = answer,
                onDismiss = { showDialog = false }
            )
        }
        Row (
            modifier = Modifier.padding(bottom = 50.dp)
                .align(Alignment.TopCenter)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiStatePost is UiState.Loading) {
                println("Hien thanh trang thai")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Text(
                        text = "Đang đăng bài: ${(progress * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (uiStatePost is UiState.Success) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng bài thành công",
                        color = Color.Green,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

//        Box(
//            modifier = Modifier.fillMaxSize().padding(bottom = 50.dp),
//            contentAlignment = Alignment.BottomEnd
//        ) {
//            Floating3DAssistant(
//                isExpanded = is3DExpanded,
//                onExpandChange = { is3DExpanded = it },
//                engine = engine,
//                // TRUYỀN DỮ LIỆU ĐÃ NẠP XUỐNG
//                modelInstance = ericModelInstance,
//                environment = globalEnvironment
//            )
//        }
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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(HelloDocAccent) // Hot brand news indicator
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = forceMarqueeText(firstHalf.getOrNull(firstIndex)?.title.orEmpty()),
                            modifier = Modifier
                                .weight(1f)
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
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                        )
                    }
                    if (secondHalf.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
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
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            maxLines = 1,
                        )
                    }
                }
            } else {
                NewsItemList(newsList = newsList, navHostController = navHostController)
            }

            IconButton(
                onClick = { showAllNews = !showAllNews },
                modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp)
            ) {
                Icon(
                    imageVector = if (showAllNews) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showAllNews) "Thu gọn" else "Xem thêm",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title, // Removed Tet emoji
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary // Use brand yellow
            )
        )
        if (onSeeAllClick != null) {
            Surface(
                onClick = onSeeAllClick,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.clickableWithScale { onSeeAllClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Xem tất cả",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.DoubleArrow,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
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
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                "Bạn muốn hỏi gì hôm nay?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.speak),
                contentDescription = "AI Assistant Icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            Surface(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(40.dp)
                    .clickableWithScale {
                        if (text.isNotBlank()) {
                            onSubmit(text)
                            text = ""
                        }
                    },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary, // Use brand yellow
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Submit",
                        tint = Color.Black, // Dark icon on yellow background
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (text.isNotBlank()) {
                                    navHostController.currentBackStackEntry?.savedStateHandle?.set("first_question", text)
                                    text = ""
                                    navHostController.navigate("gemini_help")
                                }
                            },
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        singleLine = true
    )
}

@Composable
fun Modifier.clickableWithScale(
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
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
        HorizontalDivider(color = MaterialTheme.colorScheme.secondaryContainer, thickness = 1.dp)
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
        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer, thickness = 1.dp)
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
fun GridServiceList(items: List<String>, onClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    key(item) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(85.dp)
                                .clickableWithScale { onClick(item) },
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            tonalElevation = 4.dp,
                            shadowElevation = 8.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Image(
                                            painter = painterResource(
                                                id = if (item == "Tính BMI") R.drawable.doctor else R.drawable.speak
                                            ),
                                            contentDescription = item,
                                            modifier = Modifier.size(28.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
        SectionHeader(
            title = "Chuyên khoa",
            onSeeAllClick = if (specialties.size > 6) { { showAllSpecialties = !showAllSpecialties } } else null
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(displayedSpecialties, key = { specialty -> "specialty_${specialty.id}" }) { specialty ->
                SpecialtyItem(
                    navHostController = navHostController,
                    specialty = specialty,
                    onClick = { showToast(context, "Đã chọn: ${specialty.name}") }
                )
            }
        }
    }
}

@Composable
fun SpecialtyItem(
    navHostController: NavHostController,
    specialty: GetSpecialtyResponse,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(110.dp)
            .height(150.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .clickableWithScale {
                firebaseAnalytics.logEvent("specialty_selected", bundleOf(
                    "ID_specialty" to specialty.id,
                    "Name_of_specialty" to specialty.name,
                ))
                onClick()
                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("specialtyId", specialty.id)
                    set("specialtyName", specialty.name)
                    set("specialtyDesc", specialty.description)
                }
                navHostController.navigate("doctor_list")
            },
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            // Icon Container
            Surface(
                modifier = Modifier.size(70.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!specialty.icon.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(specialty.icon)
                                .crossfade(true)
                                .build(),
                            contentDescription = specialty.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.doctor),
                            contentDescription = specialty.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Text(
                text = specialty.name,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    fontSize = 12.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SectionHeader(
            title = "Bác sĩ nổi bật",
            onSeeAllClick = if (doctors.size > 6) { { showAllDoctors = !showAllDoctors } } else null
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(displayedDoctors, key = { doctor -> "doctor_${doctor.id}" }) { doctor ->
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
}

@Composable
fun DoctorItem(doctor: GetDoctorResponse, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(150.dp)
            .clickableWithScale { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                if (!doctor.avatarURL.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(doctor.avatarURL)
                            .crossfade(true)
                            .build(),
                        contentDescription = doctor.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.doctor),
                        contentDescription = doctor.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = doctor.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = doctor.specialty.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
