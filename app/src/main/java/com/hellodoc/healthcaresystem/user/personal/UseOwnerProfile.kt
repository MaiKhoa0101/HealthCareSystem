package com.hellodoc.healthcaresystem.user.personal
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.user.post.model.ContainerPost
import com.hellodoc.healthcaresystem.user.post.model.ContentPost
import com.hellodoc.healthcaresystem.user.post.model.FooterItem
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@Composable
fun ProfileUserPage(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController
) {
    // Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    // Lấy dữ liệu user từ StateFlow
    val user by userViewModel.user.collectAsState()

    // Gọi API để fetch user từ server
    LaunchedEffect(Unit) {
        userViewModel.getUser("680355efaee44ce022490181")
    }

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
            PostUser()
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
fun PostUser() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bài viết đã đăng",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        ViewPostOwner(
            containerPost = ContainerPost(
                image = R.drawable.doctor,
                name = "Khoa xinh gái",
                lable = "bla bla"
            ),
            contentPost = ContentPost(
                content = "bla bla bla bla bla bla bla bla bla bla blabla bla bla bla bla blabla bla bla bla bla blabla bla bla bla bla blabla..."
            ),
            footerItem = FooterItem(
                name = "null",
                image = R.drawable.doctor
            )
        )
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
            model = user.userImage,
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

@Composable
fun ViewPostOwner(
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .background(backgroundColor, RectangleShape)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val horizontalGuideLine50 = createGuidelineFromTop(0.05f)
        val (iconImage, tvName, textField, readMore, sImage) = createRefs()

        Image(
            painter = painterResource(id = containerPost.image),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(45.dp)
                .constrainAs(iconImage) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(horizontalGuideLine50)
                },
            contentScale = ContentScale.Crop
        )

        Text(
            text = containerPost.name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Black
            ),
            modifier = Modifier.constrainAs(tvName) {
                top.linkTo(horizontalGuideLine50, margin = 5.dp)
                start.linkTo(iconImage.end, margin = 10.dp)
            }
        )

        Text(
            text = contentPost.content,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier.constrainAs(textField) {
                top.linkTo(iconImage.bottom, margin = 20.dp)
                start.linkTo(iconImage.start)
                end.linkTo(parent.end, margin = 20.dp)
                width = Dimension.fillToConstraints
            },
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if (expanded) "Thu gọn" else "Xem thêm",
            color = Color.Blue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(readMore) {
                    top.linkTo(textField.bottom, margin = 4.dp)
                    start.linkTo(textField.start)
                }
                .clickable { expanded = !expanded }
        )

        Image(
            painter = painterResource(id = footerItem.image),
            contentDescription = "Post Image",
            modifier = Modifier
                .height(360.dp)
                .fillMaxWidth()
                .background(Color.LightGray)
                .constrainAs(sImage) {
                    top.linkTo(readMore.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                }
        )
    }
}


