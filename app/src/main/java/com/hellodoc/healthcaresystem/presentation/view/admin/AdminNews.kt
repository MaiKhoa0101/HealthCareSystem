package com.hellodoc.healthcaresystem.presentation.view.admin

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.presentation.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCreateScreen(
    sharedPreferences: SharedPreferences,
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val viewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })

    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val adminId = jwt?.getClaim("userId")?.asString() ?: ""
    Log.d("JWT", "adminId from token: $adminId")
    // Tạo trình chọn ảnh từ thư viện
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = selectedImageUris + uris
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Quay lại",
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = "Tạo tin tức",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tiêu đề") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White
                    )
                )
            }

            // Content area
            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nhập nội dung ở đây") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White
                    )
                )
            }

            // Display selected images
            item {
                if (selectedImageUris.isNotEmpty()) {
                    Text(
                        text = "Ảnh đã chọn:",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedImageUris) { uri ->
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Ảnh đã chọn",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Nút xóa ảnh
                                IconButton(
                                    onClick = {
                                        selectedImageUris = selectedImageUris.toMutableList().apply {
                                            remove(uri)
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xóa ảnh",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Thêm ảnh button
            Button(
                onClick = {
                    // Mở trình chọn ảnh khi nhấn nút
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_gallery),
                        contentDescription = "Thêm ảnh",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thêm ảnh")
                }
            }

            // Xuất bản button
            Button(
                onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.createNews(
                            adminId = adminId,
                            title = title,
                            content = content,
                            imageUris = selectedImageUris,
                            context = context
                        ) {
                            title = ""
                            content = ""
                            selectedImageUris = emptyList()
                            viewModel.getAllNews() // cập nhật lại danh sách
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Xuất bản tin tức")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsManagerScreen(
    sharedPreferences: SharedPreferences,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { navController.navigate("CreateNews") },
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        ) {
            Text("+ Tạo tin tức mới")
        }

        NewsListScreen(sharedPreferences = sharedPreferences)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    sharedPreferences: SharedPreferences
) {
    val viewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })
    val newsList by viewModel.newsList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllNews()
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Danh sách tin tức", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (newsList.isEmpty()) {
            EmptyPostList()
        } else {
            NewsList(news = newsList, newsViewModel = viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsList(news: List<NewsResponse>, newsViewModel: NewsViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 10.dp)) {
        itemsIndexed(news) { index, item ->
            NewsItem(id = index + 1, news = item, newsViewModel = newsViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsItem(id: Int, news: NewsResponse, newsViewModel: NewsViewModel) {
    var showDetail by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isCheckingComments by remember { mutableStateOf(false) }
    var showCommentsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("your_pref_name", Context.MODE_PRIVATE)
    val viewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })
    val commentsMap by newsViewModel.newsComments.collectAsState()
    val comments = commentsMap[news.id] ?: emptyList()
    val favoriteCountMap by newsViewModel.favoriteCountMap.collectAsState()
    val totalFavorites = favoriteCountMap[news.id] ?: "0"
    val commentsCount = comments.size


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp.times(6))
                    .clickable { showDetail = true },
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    bottomStart = 16.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "ID: $id",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Tiêu đề: ${news.title}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Người tạo: Admin",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Số lượng ảnh: ${news.media.size}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Ngày tạo: ${news.createdAt.timeInVietnam()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Card(
                modifier = Modifier
                    .height(28.dp.times(6))
                    .width(100.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 16.dp,
                    bottomEnd = 16.dp
                ),
                elevation = CardDefaults.cardElevation(5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Cyan)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { showImageDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = "Ảnh",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            isCheckingComments = !isCheckingComments
                            if (isCheckingComments) {
                                newsViewModel.getComments(news.id)
                            }
                                  },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.comment),
                            contentDescription = "Bình luận",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "Xoá",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
        if (isCheckingComments && comments.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                itemsIndexed(comments) { index, comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(29.dp.times(4))
                                .clickable { showCommentsDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp) // Space between text items
                            ) {
                                Text(
                                    text = "ID: ${index + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Tên người bình luận: ${comment.user.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Nội dung: ${comment.content}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Ngày tạo: ${comment.createdAt.timeInVietnam()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            }
                        Card(
                            modifier = Modifier
                                .height(29.dp.times(4)) // Match the height of the first card
                                .width(100.dp), // Fixed width for the buttons card
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp
                            ),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0F7FA) // Màu xanh nhạt dễ nhìn
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center, // Center the buttons
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.deleteComment(comment.id, news.id) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.minus),
                                        contentDescription = "Remove",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }


                    if (showCommentsDialog) {
                        AlertDialog(
                            onDismissRequest = { showCommentsDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showCommentsDialog = false }) {
                                    Text("Đóng")
                                }
                            },
                            title = { Text("Chi tiết bình luận") },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    Text("ID: ${index + 1}", fontSize = 15.sp)
                                    Text("Tên người bình luận: ${comment.user.name}", fontSize = 15.sp)
                                    Text("Nội dung: ${comment.content}", fontSize = 15.sp)
                                    Text("Ngày tạo: ${comment.createdAt.timeInVietnam()}", fontSize = 15.sp)
                                }
                            }
                        )
                    }
                }

                }
            }
        }

        if (showDetail) {
            AlertDialog(
                onDismissRequest = { showDetail = false },
                confirmButton = {
                    TextButton(onClick = { showDetail = false }) {
                        Text("Đóng")
                    }
                },
                title = { Text("Chi tiết tin tức") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item {
                            Text("Tiêu đề: ${news.title}", fontSize = 15.sp)
                        }
                        item {
                            Text("Nội dung: ${news.content}", fontSize = 15.sp)
                        }
                        item {
                            Text("Ngày tạo: ${news.createdAt.timeInVietnam()}", fontSize = 15.sp)
                        }
                    }
                }
            )
        }
        if (showImageDialog) {
            AlertDialog(
                onDismissRequest = { showImageDialog = false },
                confirmButton = {
                    TextButton(onClick = { showImageDialog = false }) {
                        Text("Đóng")
                    }
                },
                title = { Text("Ảnh bài viết") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        news.media.forEach { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Inside
                            )
                        }
                    }
                }
            )
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteNews(news.id, context)
                        showDeleteConfirm = false
                    }) {
                        Text("Xoá")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Huỷ")
                    }
                },
                title = { Text("Xác nhận xoá") },
                text = { Text("Bạn có chắc muốn xoá bài tin tức này?") }
            )
        }
    }



