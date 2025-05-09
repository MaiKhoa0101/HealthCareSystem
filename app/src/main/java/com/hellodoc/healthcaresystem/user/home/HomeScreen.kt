package com.hellodoc.healthcaresystem.user.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hellodoc.core.common.utils.PhoneCallUtils
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
//import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse2
import com.hellodoc.healthcaresystem.responsemodel.GetFAQItemResponse
import com.hellodoc.healthcaresystem.responsemodel.GetMedicalOptionResponse
import com.hellodoc.healthcaresystem.responsemodel.GetRemoteMedicalOptionResponse
import com.hellodoc.healthcaresystem.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.user.notification.timeAgoInVietnam
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.ViewPostOwner
import com.hellodoc.healthcaresystem.user.personal.userModel
import com.hellodoc.healthcaresystem.user.post.userId

import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.RemoteMedicalOptionViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthMateHomeScreen(
    modifier: Modifier = Modifier,
    sharedPreferences: SharedPreferences,
    onNavigateToDoctorList: (String, String, String) -> Unit,
    navHostController: NavHostController
) {

    val context = LocalContext.current

    val faqItemViewModel: FAQItemViewModel = viewModel(factory = viewModelFactory {
        initializer { FAQItemViewModel(sharedPreferences) }
    })
    val faqItems by faqItemViewModel.faqItems.collectAsState()

    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })
    val doctors by doctorViewModel.doctors.collectAsState()

    val specialtyViewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })
    val specialties by specialtyViewModel.specialties.collectAsState()

    val medicalOptionViewModel: MedicalOptionViewModel = viewModel(factory = viewModelFactory {
        initializer { MedicalOptionViewModel(sharedPreferences) }
    })
    val medicalOptions by medicalOptionViewModel.medicalOptions.collectAsState()

    val remoteMedicalOptionViewModel: RemoteMedicalOptionViewModel = viewModel(factory = viewModelFactory {
        initializer { RemoteMedicalOptionViewModel(sharedPreferences) }
    })
    val remoteMedicalOptions by remoteMedicalOptionViewModel.remoteMedicalOptions.collectAsState()

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val posts by postViewModel.posts.collectAsState()

    val geminiViewModel: GeminiViewModel = viewModel(factory = viewModelFactory {
        initializer { GeminiViewModel(sharedPreferences) }
    })
    val question by geminiViewModel.question.collectAsState()
    val answer by geminiViewModel.answer.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val newsViewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })
    val newsList by newsViewModel.newsList.collectAsState()


    LaunchedEffect(Unit) {
        doctorViewModel.fetchDoctors()
        specialtyViewModel.fetchSpecialties()
        launch {
            medicalOptionViewModel.fetchMedicalOptions()
        }

        launch {
            remoteMedicalOptionViewModel.fetchRemoteMedicalOptions()
        }

        launch {
            newsViewModel.getAllNews()
        }
        postViewModel.getAllPosts()

//        userName = viewModel.getUserNameFromToken()
//        role = viewModel.getUserRole()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .background(Color(0xFF00C5CB))
                        .padding(16.dp)
                ) {
                    AssistantQueryRow(
                        navHostController,
                        onSubmit = { query ->
                            geminiViewModel.askGemini(query)
                            showDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (newsList.isEmpty()) {
                        EmptyList("tin mới")
                    } else {
                        NewsItemList(newsList)
                    }

                }
            }

            // Dịch vụ toàn diện
            item {
                SectionHeader(title = "Dịch vụ toàn diện")
                if (medicalOptions.isEmpty()) {
                    EmptyList("dịch vụ hệ thống")
                } else {
                    GridServiceList(medicalOptions) { medicalOption ->
                        when (medicalOption.name) {
                            "Tính BMI" -> navHostController.navigate("bmi-checking")
                            else -> {

                            }
                        }
                    }
                }
            }

            // Chuyên khoa
            item {
                SectionHeader(title = "Chuyên khoa")

            if (specialties.isEmpty()) {
                EmptyList("chuyên khoa")
            } else {
                SpecialtyList(context,specialties = specialties, onNavigateToDoctorList = onNavigateToDoctorList)
            }
        }

            // Bác sĩ nổi bật
            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (doctors.isEmpty()) {
                    EmptyList("bác sĩ")
                } else {
                    println("ko co bi empty")
                    DoctorList(navHostController = navHostController, doctors = doctors)
                }
            }

            // Khám từ xa
//            item {
//                SectionHeader(title = "Khám từ xa")
//
//                if (remoteMedicalOptions.isEmpty()) {
//                    EmptyList("dịch vụ khám từ xa")
//                } else {
//                    RemoteMedicalOptionList(context, remoteMedicalOptions = remoteMedicalOptions)
//                }
//            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (posts.isNotEmpty()) {
                    posts.forEach { postItem ->
                        ViewPostOwner(
                            postId = postItem.id,
                            containerPost = ContainerPost(
                                id = postItem.user.id,
                                name = postItem.user.name,
                                imageUrl = postItem.user.avatarURL ?: ""
                            ),
                            contentPost = ContentPost(postItem.content),
                            footerItem = FooterItem(imageUrl = postItem.media.joinToString("|")),
                            createdAt = postItem.createdAt,
                            postViewModel = postViewModel,
                            currentUserId = userId,
                            navHostController = navHostController,
//                            onClickReport = onClickReport,
//                            onShowComment = onShowComment
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showDialog) {
            AssistantAnswerDialog(
                question = question,
                answer = answer,
                onDismiss = { showDialog = false }
            )
        }
    }
}

// Hàm này đảm bảo Toast không vi phạm quy tắc của Compose
fun showToast(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun EmptyList(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
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
            LazyColumn(
                modifier = Modifier.heightIn(min = 100.dp, max = 300.dp)
            ) {
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
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Đặt câu hỏi cho Trợ lý AI", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(id = R.drawable.submit_arrow),
            contentDescription = "submit question for AI",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    if (text.isNotBlank()) {
//                        onSubmit(text) // Gửi dữ liệu
                        navHostController.currentBackStackEntry?.savedStateHandle?.set("first_question", text)
                        text = "" // Xóa text sau khi gửi
                        navHostController.navigate("gemini_help")
                    }
                }
        )
    }
}
@Composable
fun NewsItemList(
    newsList: List<NewsResponse>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        newsList.forEach { news ->
            NewsItem(news = news, onSelectNews = {

            })
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
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = news.title,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Xem chi tiết",
                tint = Color.Black,
                modifier = Modifier.clickable { onSelectNews() }
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.White, thickness = 1.dp)
    }
}



@Composable
fun FAQItemList(context: Context, faqItems: List<GetFAQItemResponse>) {
    faqItems.forEach { faqItem ->
        FAQItem(faqItem) {
            showToast(context, "Clicked: ${faqItem.question}")
        }
    }
}

@Composable
fun FAQItem(
    faq: GetFAQItemResponse,
    onSelectQuestion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = faq.question,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f) // Giữ khoảng cách với icon
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Expand",
                tint = Color.Black,
                modifier = Modifier.clickable { onSelectQuestion() }
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.White, thickness = 1.dp) // Đường kẻ ngang
    }
}

@Composable
fun GridServiceList(items: List<GetMedicalOptionResponse>, onClick: (GetMedicalOptionResponse) -> Unit) {
    Column (modifier = Modifier.padding(horizontal = 16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .clickable { onClick(item) },
                    ) {
                        Row(verticalAlignment =  Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.doctor),
                                contentDescription = item.name,
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = item.name, color = Color.Black)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SpecialtyList(context: Context, specialties: List<GetSpecialtyResponse>, onNavigateToDoctorList: (String, String, String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        items(specialties) { specialty ->
            SpecialtyItem(
                specialty = specialty,
                onClick = {
                    showToast(context, "Đã chọn: ${specialty.name}")
                },
                onNavigateToDoctorList = onNavigateToDoctorList
            )
        }
    }
}

@Composable
fun SpecialtyItem(
    specialty: GetSpecialtyResponse,
    onClick: () -> Unit,
    onNavigateToDoctorList: (String, String, String) -> Unit
) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFCCCCCC), // Màu viền xám nhẹ
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
                onNavigateToDoctorList(specialty.id, specialty.name, specialty.description)
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
                    model = specialty.icon,
                    contentDescription = specialty.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = specialty.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = specialty.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
fun DoctorList(
    navHostController: NavHostController,
    doctors: List<GetDoctorResponse>,
    onSeeMoreClick: () -> Unit = {} // callback cho "Xem thêm"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 14.dp)
            .background(Color(0xFF73E3E7))
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
                color = Color.Black
            )

//            Text(
//                text = "Xem thêm",
//                fontSize = 14.sp,
//                color = Color(0xFF0085FF),
//                modifier = Modifier
//                    .clickable { onSeeMoreClick() }
//            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(doctors) { doctor ->
                DoctorItem(doctor) {
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
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!doctor.avatarURL.isNullOrBlank()) {
            AsyncImage(
                model = doctor.avatarURL,
                contentDescription = doctor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = doctor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = doctor.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = doctor.specialty.name,
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RemoteMedicalOptionList(context: Context, remoteMedicalOptions: List<GetRemoteMedicalOptionResponse>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        items(remoteMedicalOptions) { remoteMedicalOption ->
            RemoteMedicalOption(remoteMedicalOption) {
                showToast(context, "Clicked: ${remoteMedicalOption.name}")
            }
        }
    }
}

@Composable
fun RemoteMedicalOption(service: GetRemoteMedicalOptionResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .background(Color(0xFFFBE9E7), shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),

        contentAlignment = Alignment.Center // Căn giữa nội dung trong Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = service.name,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = service.name, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewPostOwner(
    postId: String,
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    createdAt: String,
    postViewModel: PostViewModel,
    currentUserId: String,
    navHostController: NavHostController,
//    onClickReport: (String) -> Unit,
//    onShowComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    var isCommenting by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }
    var shouldFetchComments by remember { mutableStateOf(false) }

    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()

    val isFavorited = isFavoritedMap[postId] ?: false
    val totalFavorites = totalFavoritesMap[postId] ?: "0"

    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()
    var showPostReportBox by remember { mutableStateOf(false) }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(editingCommentId) {
        if (editingCommentId == null) {
            // Sau khi lưu xong và thoát khỏi chế độ sửa, cập nhật lại UI
            postViewModel.fetchComments(postId)
        }
    }
    LaunchedEffect(postId) {
        // Gọi API và cập nhật state khi dữ liệu được fetch về
        postViewModel.fetchFavoriteForPost(postId, userId)
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val mediaList = footerItem.imageUrl.split("|").filter { it.isNotBlank() }

    LaunchedEffect(shouldFetchComments) {
        if (shouldFetchComments) {
            coroutineScope.launch {
                postViewModel.fetchComments(postId)
                shouldFetchComments = false
            }
        }
    }
    var shouldShowSeeMore by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = modifier
                .background(backgroundColor, RectangleShape)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            // Row for Avatar and Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navHostController.navigate("post-detail/$postId") },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // để dồn 2 phần trái - phải
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = containerPost.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = containerPost.name,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = createdAt.timeAgoInVietnam(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { showPostReportBox = !showPostReportBox },
                    modifier = Modifier
                        .padding(end = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }

            // Content bài viết
            SubcomposeLayout { constraints ->
                val measuredText = subcompose("text") {
                    Text(
                        text = contentPost.content,
                        style = TextStyle(fontSize = 16.sp),
                        maxLines = Int.MAX_VALUE,
                        onTextLayout = {
                            shouldShowSeeMore = it.lineCount > 2
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }[0].measure(constraints)

                val actualText = subcompose("displayText") {
                    Text(
                        text = contentPost.content,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp)
                    )
                }[0].measure(constraints)

                val seeMoreText = if (shouldShowSeeMore) {
                    subcompose("seeMore") {
                        Text(
                            text = if (expanded) "Thu gọn" else "Xem thêm",
                            color = Color.Blue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { expanded = !expanded }
                        )
                    }[0].measure(constraints)
                } else null

                layout(constraints.maxWidth, actualText.height + (seeMoreText?.height ?: 0)) {
                    actualText.placeRelative(0, 0)
                    seeMoreText?.placeRelative(0, actualText.height)
                }
            }
            //anh
            if (mediaList.isNotEmpty()) {
                HorizontalPager(
                    count = mediaList.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) { page ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = mediaList[page],
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray)
                        )
                        // Ô số thứ tự ảnh
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${page + 1}/${mediaList.size}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // ICON like & comment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // LIKE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        postViewModel.updateFavoriteForPost(
                            postId = postId,
                            userId = currentUserId,
                            userModel = userModel
                        )
//                    isFavorited = !isFavorited
                    }
                ) {
                    Icon(
                        painter = painterResource(id = if (isFavorited) R.drawable.liked else R.drawable.like),
                        contentDescription = "Like",
                        tint = if (isFavorited) Color.Red else Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$totalFavorites Likes", fontSize = 18.sp)
                }

                // COMMENT
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
//                        onShowComment(postId)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.comment),
                        contentDescription = "Comment",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comment", fontSize = 18.sp)
                }
            }

            // UI COMMENT

        }
        val context = LocalContext.current
        if (showPostReportBox) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 50.dp, end = 8.dp)
                    .width(220.dp)
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .border(5.dp, Color.LightGray)
                    .padding(12.dp)
            ) {
                // Tố cáo
                Column(
                    modifier = Modifier
                        .clickable {
                            showPostReportBox = false
//                            onClickReport(postId)
                        }
                        .padding(8.dp)
                ) {
                    Text("Tố cáo bài viết", fontWeight = FontWeight.Bold)
                    Text("Bài viết có nội dung vi phạm", fontSize = 13.sp, color = Color.Gray)
                }

                // Chỉ hiển thị nút XÓA nếu là chính người đăng
                if (currentUserId == containerPost.id) {
                    Divider(thickness = 3.dp, color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                    Column(
                        modifier = Modifier
                            .clickable {
                                showPostReportBox = false
                                postViewModel.deletePost(postId)
                            }
                            .padding(8.dp)
                    ) {
                        Text("Xóa bài viết", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Xóa khỏi cuộc đời của bạn", fontSize = 13.sp, color = Color.Gray)
                    }
                    Divider(thickness = 3.dp, color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                    Column(
                        modifier = Modifier
                            .clickable {
                                showPostReportBox = false
                                val intent = Intent(context, HomeActivity::class.java).apply {
                                    putExtra("navigate-to", "edit_post/$postId")
                                }
                                context.startActivity(intent)
                            }
                            .padding(8.dp)
                    ) {
                        Text("Sửa bài viết", fontWeight = FontWeight.Bold, color = Color.Blue)
                        Text("Gáy xong rồi sửa", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}