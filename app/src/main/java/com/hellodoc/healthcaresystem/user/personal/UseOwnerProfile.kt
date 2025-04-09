package com.hellodoc.healthcaresystem.user.personal
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hellodoc.healthcaresystem.user.post.model.ContainerPost
import com.hellodoc.healthcaresystem.user.post.model.ContentPost
import com.hellodoc.healthcaresystem.user.post.model.FooterItem
import com.hellodoc.healthcaresystem.R

@Preview (showBackground = true)
@Composable
fun ProfileUserPage(){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally // ❌ dòng này không hợp lệ cho LazyColumn
    ) {
        item {
            ProfileSection()
        }
        item {
            PostUser()
        }
    }
}


@Composable
fun ProfileSection(){
    Column(
        modifier = Modifier
        .background(Color.Cyan)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            UserIntroSection()
            Spacer(modifier = Modifier.height(26.dp))
            UserProfileModifierSection()
        }
    }
}

@Composable
fun PostUser(){
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    color = Color.Gray,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally
            )
        {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bài viết đã đăng",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        ViewPostOwner(
            containerPost = ContainerPost(
                image = R.drawable.doctor,
                name = "Khoa xinh gái",
                lable = "bla bla  "
            ),
            contentPost = ContentPost(
                content = "bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla"
            ),
            footerItem = FooterItem(
                name = "null",
                image = R.drawable.doctor
            )
        )
    }
}



@Composable
fun UserIntroSection(){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ){
        Image(painter = painterResource(R.drawable.doctor), contentDescription = "avt user")
        Text("Mai Nguyễn Đăng Khoa", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("@mndk2015")
    }
}

@Composable
fun UserProfileModifierSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { },
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
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {  },
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
                textAlign = TextAlign.Center,
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
            .background(color = backgroundColor, shape = RectangleShape)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val horizontalGuideLine50 = createGuidelineFromTop(0.05f)
        val (iconImage, tvName, textField, readMore, sImage) = createRefs()

        Image(
            painter = painterResource(id = containerPost.image),
            contentDescription = null,
            modifier = Modifier
                .clip(shape = CircleShape)
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

