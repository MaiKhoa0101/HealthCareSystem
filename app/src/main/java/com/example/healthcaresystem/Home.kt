package com.example.healthcaresystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import kotlinx.serialization.Serializable

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Index(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
//@Composable
//fun AppNavigation(navController: NavController, modifier: Modifier = Modifier) {
//    NavHost(navController = navController, startDestination = ScreenIndex) {
//        composable<ScreenIndex> { Index(navController) }
//        composable<ScreenInfo> { InfoScreen(navController) }
//    }
//}
@Composable
fun Index(modifier: Modifier =Modifier) {
    Box(modifier = Modifier.padding(top = 45.dp)) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            HomeScreen()
        }
        SidebarMenu()
    }
}
@Composable
fun SidebarMenu() {
    var isExpanded by remember { mutableStateOf(false) }
    val widthAnim by animateDpAsState(targetValue = if (isExpanded) 200.dp else 0.dp)
    val alphaAnim by animateFloatAsState(targetValue = if (isExpanded) 1f else 0f)

    Column(
        Modifier
            .fillMaxHeight()
            .width(widthAnim)
            .background(Color.Blue.copy(alpha = alphaAnim))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Icon(
            Icons.Default.Menu,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .size(40.dp),
            contentDescription = null,
            tint = Color.White
        )

        if (isExpanded) {
            Spacer(modifier = Modifier.height(20.dp))
            DrawerItem(Icons.Default.Person, "Thông tin", isExpanded)
            DrawerItem(Icons.Default.Settings, "Cài đặt", isExpanded)
            DrawerItem(Icons.Default.Info, "Topic", isExpanded)
            DrawerItem(Icons.Default.Search, "Khám bệnh", isExpanded)
        }
    }
}
@Composable
fun HomeScreen() {
    Column()
    {
        Headbar()
        Box() {
            Column (
                modifier = Modifier
                    .size(400.dp,190.dp)
                    .background(color = Color.Cyan),
                horizontalAlignment =Alignment.CenterHorizontally
            ){
                CommonSpace()
                TextField()
                Box() {
                    CustomPainterImageCompose()
                    UnderTextFieldImage()
                }
                Spacer(modifier = Modifier.height(15.dp))
                TextFind()
            }
        }
        Box(){
            Column(
                modifier = Modifier
                    .size(400.dp,400.dp)
                    .background(color = Color.Yellow),
                horizontalAlignment =Alignment.CenterHorizontally
            ) {
                Service()
                UnderService()
            }
        }
        Menu()
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DrawerItem(icon: ImageVector, title: String, expanded: Boolean){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White)
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) togetherWith fadeOut(
                    tween(150)
                ) using SizeTransform { initialSize, targetSize ->
                    keyframes {
                        IntSize(targetSize.width, initialSize.height) at 150
                        durationMillis = 300
                    }
                }
            }
        ) {
                targetState ->
            if (targetState){
                Row(Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(title, color = Color.White)
                }
            }
        }

    }
}
@Composable
fun Headbar() {
    Box(
        modifier = Modifier
            .size(400.dp, 70.dp)
            .background(color = Color.Magenta)
    ) {
        Icon(
            Icons.Default.Menu,
            modifier = Modifier
                .size(50.dp)
                .padding(start = 10.dp, top = 7.dp),
            contentDescription = null,
            tint = Color.White
        )
        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = "Logo Icon",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .clickable {  },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.time_icon),
                contentDescription = "Time Icon",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "Lịch hẹn",
                fontSize = 12.sp,
                color = Color.Cyan
            )
        }
    }
}

@Composable
fun TextField(){
    var QUEST by remember {
        mutableStateOf("")
    }
    TextField(
        value = QUEST,
        onValueChange = { newValue ->
            QUEST = newValue
        },
        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold),
        label = {
            Text("Đặt câu hỏi cho trợ lý AI",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 18.sp,
                    )
                )}, //day label len tren khi nhap ban phim
        placeholder ={ Text("Hỏi theo cách của bạn")},
        modifier = Modifier.width(370.dp),
        trailingIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "send",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )}
        },// hien thi sau
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor  = Color.White,
            disabledContainerColor  = Color.White,
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        )
}

@Composable
fun UnderTextFieldImage(){
    Row(modifier = Modifier.clickable {  }) {
        Image(
            modifier = Modifier.padding(start = 10.dp),
            imageVector = Icons.Default.LocationOn,
            contentDescription = "location",
            )
        Text("Chọn Bệnh viện - phòng khám", color = Color.Gray)
    }

}

@Composable
fun CustomPainterImageCompose(){
    Image(ColorPainter(Color.White),
        contentDescription = null,
        modifier = Modifier
            .size(370.dp,30.dp)
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)))
}

@Composable
fun TextFind(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.Start) {
        Row(modifier = Modifier.clickable {  }) {
            Text("Gì cũng được",
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(20f, fill = false)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.padding(start = 10.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "location",
            )
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(modifier = Modifier.clickable {  }) {
            Text("Làm sao để cưới nhiều vợLàm sao để cưới nhiều vợLàm sao để cưới nhiều vợLàm sao để cưới nhiều vợ",
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(20f, fill = false)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.padding(start = 10.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "location",
            )
        }
    }

}

@Composable
fun Service(){
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 7.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("Dịch vụ toàn diện",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text("Xem thêm", color = Color.Cyan, modifier = Modifier.clickable {  })
        }
        Box(modifier = Modifier
            .padding(10.dp)
            .size(170.dp,70.dp)
            .background(color = Color.White)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxSize()
            .clickable {  },
            contentAlignment = Alignment.Center
        )
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(id = R.drawable.avarta1),
                    contentDescription = "circle avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 5.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Khám Chuyên khoaKhám Chuyên khoKhám Chuyên kho",
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun UnderService(){
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 7.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("Chuyên khoa",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text("Xem thêm", color = Color.Cyan, modifier = Modifier.clickable {  })
        }
        Box(modifier = Modifier
            .padding(10.dp)
            .size(150.dp,130.dp)
            .background(color = Color.White)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxSize()
            .clickable {  },
            contentAlignment = Alignment.Center
        )
        {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painterResource(id = R.drawable.avarta1),
                    contentDescription = "circle avatar",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cơ xương khớpCustomize Toolbar...Customize Toolbar...",
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun BoxItem(
    modifier: Modifier = Modifier,
    color: Color,
    width: Dp = 80.dp,
    height: Dp = 70.dp,
    text: String,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .size(width, height)
            .background(color = color)
            .clickable {  },
        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = "home", Modifier.size(24.dp))
            Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    backgroundColor: Color = Color.Cyan,
    iconColor: Color = Color.White,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 5.dp, shape = CircleShape)
            .background(backgroundColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add",
            tint = iconColor,
            modifier = Modifier.size(size * 0.8f)
        )
    }
}

@Composable
fun Menu() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.Cyan)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoxItem(color = Color.Cyan, text = "Trang chủ", icon = Icons.Default.Home)
                BoxItem(color = Color.Cyan, text = "Tìm kiếm", icon = Icons.Default.Search)
                Spacer(modifier = Modifier.width(56.dp))
                BoxItem(color = Color.Cyan, text = "Thông báo", icon = Icons.Default.MailOutline)
                BoxItem(color = Color.Cyan, text = "Cá nhân", icon = Icons.Default.Person)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-37).dp)
        ) {
            CircleButton(onClick = {})
        }
    }
}

@Composable
fun CommonSpace(){
    Spacer(modifier = Modifier.height(10.dp))
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    HealthCareSystemTheme {
        Index()
    }
}
}