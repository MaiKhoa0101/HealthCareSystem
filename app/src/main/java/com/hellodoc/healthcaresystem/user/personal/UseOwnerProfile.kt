package com.hellodoc.healthcaresystem.user.personal
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@Preview(showBackground = true)
@Composable
fun PreviewProfileUserPage() {
    // Fake SharedPreferences
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("preview_prefs", Context.MODE_PRIVATE)

    // Fake NavController
    val navController = rememberNavController()

    ProfileUserPage(
        sharedPreferences = sharedPreferences,
        navHostController = navController
    )
}


@Composable
fun ProfileUserPage(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController
) {
    // Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val post by postViewModel.posts.collectAsState()

    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val userId = jwt?.getClaim("userId")?.asString()

    // Gọi API để fetch user từ server
    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.getUser(it)
            postViewModel.getPostUserById(it)
        }

    }
    // Lấy dữ liệu user từ StateFlow
    val user by userViewModel.user.collectAsState()
    // Nếu chưa có user (null) thì không hiển thị giao diện
    if (user == null) {
        println("user == null")
        return
    }

    // Nếu có user rồi thì hiển thị UI
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            ProfileSection(navHostController, user!!)
        }
        item {
            PostUser(post)
        }
    }
}

@Composable
fun ProfileSection(navHostController: NavHostController, user: User) {
    Column(
        modifier = Modifier.background(Color.Cyan)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            UserIntroSection(user)
            Spacer(modifier = Modifier.height(26.dp))
            UserProfileModifierSection(navHostController, user)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}





@Composable
fun UserIntroSection(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // Hiển thị ảnh đại diện
        AsyncImage(
            model = user.avatarURL,
            contentDescription = "Avatar",
            modifier = Modifier
                .height(140.dp)
                .padding(10.dp)
                .clip(CircleShape)
        )

        // Tên và email người dùng
        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(user.email)
    }
}

@Composable
fun UserProfileModifierSection(navHostController: NavHostController, user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { navHostController.navigate("editProfile") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(40.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Chỉnh sửa hồ sơ",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = { navHostController.navigate("doctorRegister") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(40.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Quản lý phòng khám",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}





