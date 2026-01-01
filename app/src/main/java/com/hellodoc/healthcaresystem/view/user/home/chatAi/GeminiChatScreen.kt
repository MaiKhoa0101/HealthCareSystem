package com.hellodoc.healthcaresystem.view.user.home.chatAi

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.MessageType
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ChatMessage


@Composable
fun GeminiChatScreen(
    navHostController: NavHostController
) {
//    val savedStateHandle = navHostController.currentBackStackEntry?.savedStateHandle
    val geminiViewModel: GeminiViewModel = hiltViewModel()
    // Cập nhật giá trị nếu có trong savedStateHandle
    val savedStateHandle = navHostController
        .previousBackStackEntry
        ?.savedStateHandle

    // Lấy first_question khi màn hình được mở
    LaunchedEffect(savedStateHandle) {
        val question = savedStateHandle?.get<String>("first_question")
        if (!question.isNullOrBlank()) {
            geminiViewModel.processUserQuery(question)
            savedStateHandle?.remove<String>("first_question") // clear key sau khi dùng
        }
    }
    val chatMessages by geminiViewModel.chatMessages.collectAsState()
    var input by remember { mutableStateOf("") }
    val groupedMessages = remember(chatMessages) {
        mutableListOf<List<ChatMessage>>().apply {
            var temp = mutableListOf<ChatMessage>()
            var currentType: MessageType? = null

            for (msg in chatMessages.reversed()) {
                if (msg.type == MessageType.ARTICLE || msg.type == MessageType.DOCTOR) {
                    if (currentType == null || currentType == msg.type) {
                        temp.add(msg)
                        currentType = msg.type
                    } else {
                        add(temp.toList())
                        temp.clear()
                        temp.add(msg)
                        currentType = msg.type
                    }
                } else {
                    if (temp.isNotEmpty()) {
                        add(temp.toList())
                        temp.clear()
                        currentType = null
                    }
                    add(listOf(msg))
                }
            }
            if (temp.isNotEmpty()) add(temp.toList())
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(title = "Trợ lý AI", onClick = { navHostController.popBackStack() })
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true
        ) {
            items(groupedMessages) { group ->
                when (group.first().type) {
                    MessageType.ARTICLE -> {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(group.reversed(), key = { it.id }) { articleMsg ->
                                ArticleBubble(
                                    title = articleMsg.message ?: "Không có tiêu đề",
                                    author = articleMsg.articleAuthor ?: "Ẩn danh",
                                    imageUrl = articleMsg.articleImgUrl,
                                    onClick = {
                                        articleMsg.articleId?.let { id ->
                                            navHostController.navigate("post-detail/$id") {
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    MessageType.DOCTOR -> {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(group, key = { it.id }) { doctorMsg ->
                                DoctorBubble(
                                    name = doctorMsg.doctorName ?: "",
                                    specialty = doctorMsg.doctorSpecialty?.name ?: "",
                                    hospital = doctorMsg.doctorHospital ?: "",
                                    avatarUrl = doctorMsg.doctorAvatar,
                                    onClick = {
                                        doctorMsg.doctorId?.let { id ->
                                            navHostController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("doctorId", id)
                                            navHostController.navigate("other_user_profile")
                                        }
                                    }
                                )
                            }
                        }
                    }
                    MessageType.TEXT -> {
                        val msg = group.first()
                        ChatBubble(msg.message, msg.isUser)
                    }
                }
            }
        }

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập câu hỏi...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    )
                )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowUp,
                contentDescription = "submit question for AI",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        if (input.isNotBlank()) {
                            geminiViewModel.processUserQuery(input)
                            input = ""
                        }
                    },
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun DoctorBubble(
    name: String,
    specialty: String?,
    hospital: String?,
    avatarUrl: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 100.dp, max = 200.dp)
                .heightIn(min = 100.dp, max = 200.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {

            // Thông tin bác sĩ
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // Avatar bác sĩ
                    if (!avatarUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Doctor avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_24), // thay bằng icon bác sĩ
                            contentDescription = "Doctor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!specialty.isNullOrEmpty()) {
                        Text(
                            text = "Chuyên khoa: " + specialty.uppercase(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!hospital.isNullOrEmpty()) {
                    Text(
                        text = hospital,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        "Xem chi tiết "
                    )
                    Icon(
                        imageVector = Icons.Default.DoubleArrow,
                        contentDescription = "submit question for AI",
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 250.dp)
                .background(
                    if (isUser) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(12.dp)
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}


@Composable
fun TopBar(title: String,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
//            .statusBarsPadding()
            .height(56.dp)
    ) {
        // Nút quay lại
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() }
        )

        // Tiêu đề ở giữa
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ArticleBubble(
    title: String,
    author: String,
    imageUrl: String?,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .heightIn(min = 280.dp, max = 280.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column {
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = if (imageUrl!= null){
                            imageUrl
                        } else {
                            Icons.Default.Image
                        },
                        contentDescription = "Article image",
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                }

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Tác giả: $author",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Xem chi tiết ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
