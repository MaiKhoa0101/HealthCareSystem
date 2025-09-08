package com.hellodoc.healthcaresystem.user.home.chatAi

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.MessageType
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance.doctor
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel

@Composable
fun GeminiChatScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {
//    val savedStateHandle = navHostController.currentBackStackEntry?.savedStateHandle
    val geminiViewModel: GeminiViewModel = viewModel(factory = viewModelFactory {
        initializer { GeminiViewModel(sharedPreferences) }
    })

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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(title = "Trợ lý AI", onClick = { navHostController.popBackStack() })
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true
        ) {
            items(
                items = chatMessages.reversed(),
                key = { it.id }
            ) { msg ->
                when (msg.type) {
                    MessageType.TEXT -> {
                        ChatBubble(msg.message, msg.isUser)
                    }
                    MessageType.ARTICLE -> {
                        ArticleBubble(
                            title = msg.message ?: "Không có tiêu đề",
                            author = msg.articleAuthor ?: "Ẩn danh",
                            imageUrl = msg.articleImgUrl,
                            onClick = {
                                msg.articleId?.let { id ->
                                    navHostController.navigate("post-detail/$id"){
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                    MessageType.DOCTOR -> {
                        DoctorBubble(
                            name = msg.message.substringBefore(" - "),
                            specialty = msg.message.substringAfter(" - ").substringBefore("(").trim(),
                            hospital = msg.message.substringAfter("(").substringBefore(")").trim(),
                            onClick = {
                                msg.doctorId?.let { id ->
                                    navHostController.currentBackStackEntry?.savedStateHandle?.set("doctorId", id)
                                    navHostController.navigate("other_user_profile")
                                }
                            }
                        )
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
                placeholder = { Text("Nhập câu hỏi...") }
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
                .widthIn(max = 300.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Spacer(modifier = Modifier.width(12.dp))

                // Thông tin bác sĩ
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!specialty.isNullOrEmpty()) {
                        Text(
                            text = specialty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!hospital.isNullOrEmpty()) {
                        Text(
                            text = hospital,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "View doctor profile",
                    tint = MaterialTheme.colorScheme.primary
                )
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
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                .background(
                    if (isUser) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
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
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
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

@Preview(showBackground = true, name = "Gemini Chat Screen Preview")
@Composable
fun GeminiChatScreenPreview() {
    val fakeNavController = rememberNavController()
    val fakePrefs = FakeSharedPreferences()
    GeminiChatScreen(
        navHostController = fakeNavController,
        sharedPreferences = fakePrefs
//        onDateTimeSelected = { date, time ->
//            // Cho preview thì mình không cần xử lý gì ở đây
//        }
    )
}

class FakeSharedPreferences : SharedPreferences {
    override fun getAll(): MutableMap<String, Any?> = mutableMapOf()

    override fun getString(key: String?, defValue: String?): String? = "preview_user"

    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?
    ): MutableSet<String>? = defValues

    override fun getInt(key: String?, defValue: Int): Int = defValue

    override fun getLong(key: String?, defValue: Long): Long = defValue

    override fun getFloat(key: String?, defValue: Float): Float = defValue

    override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue

    override fun contains(key: String?): Boolean = false

    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        override fun putString(key: String?, value: String?): SharedPreferences.Editor = this
        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): SharedPreferences.Editor = this

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this
        override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this
        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this
        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this
        override fun remove(key: String?): SharedPreferences.Editor = this
        override fun clear(): SharedPreferences.Editor = this
        override fun commit(): Boolean = true
        override fun apply() {}
    }

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {}

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {}
}
