package com.hellodoc.healthcaresystem.user.home.chatAi

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
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
        .previousBackStackEntry  // ✅ lấy từ previous
        ?.savedStateHandle

    LaunchedEffect(Unit) {
        val question = savedStateHandle?.get<String>("first_question")
        if (!question.isNullOrBlank()) {
            geminiViewModel.askGemini(question)
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
                key = { it.hashCode() }
            ) { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // <- hàng rộng ra toàn màn hình
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 250.dp) // <- Giới hạn chiều rộng tối đa của hộp chat
                            .background(
                                if (msg.isUser) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.message,
                            color = MaterialTheme.colorScheme.onBackground
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
            Image(
                painter = painterResource(id = R.drawable.submit_arrow),
                contentDescription = "submit question for AI",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        if (input.isNotBlank()) {
                            geminiViewModel.askGemini(input)
                            input = ""
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
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
            color = MaterialTheme.colorScheme.background,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
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
