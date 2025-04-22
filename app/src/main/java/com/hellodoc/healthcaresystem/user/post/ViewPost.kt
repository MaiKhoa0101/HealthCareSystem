package com.hellodoc.healthcaresystem.user.post.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme

@Composable
fun ViewBanner(modifier: Modifier = Modifier) {
    val backgroundColor = Color.White
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(80.dp)
            .fillMaxWidth()
    ) {
        val (tvName, bottomLine) = createRefs()
        Text(
            text = "Bài viết đã đăng",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = Color.Black
            ),
            modifier = Modifier.constrainAs(tvName) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Box(
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth()
                .background(Color.Gray)
                .constrainAs(bottomLine) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}

@Composable
fun ViewPost(
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



@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    HealthCareSystemTheme {
        ViewPost(
            containerPost = ContainerPost(
                image = R.drawable.img,
                name = "Khoa xinh gái",
                lable = "bla bla"
            ),
            contentPost = ContentPost(
                content = "bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla "
            ),
            footerItem = FooterItem(
                name = "null",
                image = R.drawable.avarta
            )
        )
    }
}