package com.example.healthcaresystem.User.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.healthcaresystem.R
import com.example.healthcaresystem.User.profile.ui.theme.HealthCareSystemTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfileScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.padding(top = 45.dp)) {
        UserInfo(
            icon =R.drawable.img,
            name = "Khoa xinh gái",
            email = "@mavel_killer",
            followerValue= 69,
            followerString= "followers",
            followingValue= 3,
            followingString= "following",
            likeValue= 70,
            likeString= "likes",
            buttonProf = "Chỉnh sửa hồ sơ",
            buttonActivity = "Lịch sử hoạt động",
            buttonSchedule = "Lịch khám"
        )
    }
}

@Composable
fun UserInfo(
    @DrawableRes icon: Int,
    name: String,
    email: String,
    followerValue: Int,
    followerString: String,
    followingValue: Int,
    followingString: String,
    likeValue: Int,
    likeString: String,
    buttonProf: String,
    buttonActivity: String,
    buttonSchedule: String,
    modifier: Modifier = Modifier
){
    val backgroundColor = Color.Cyan
    ConstraintLayout(
        modifier=modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(380.dp)
            .fillMaxWidth()
    ) {
        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine50 = createGuidelineFromTop(0.5f)
        val horizontalGuideLine40Bot = createGuidelineFromBottom(0.4f)
        val imgIcon = createRef()
        Image(
            painterResource(id = icon),
            contentDescription = null,
            modifier =Modifier
                .clip(shape = CircleShape)
                .size(110.dp)
                .constrainAs(imgIcon){
                top.linkTo(parent.top)
                start.linkTo(parent.start) //phải định nghĩa ít nhất 2 cái
                end.linkTo(parent.end)
                bottom.linkTo(horizontalGuideLine50)
//                height = Dimension.fillToConstraints //keo anh dai het height
            },
            contentScale = ContentScale.Crop
        )
        val (tvEmail, tvName, tvNFollower, tvFollowers, tvNFollowing, tvFollowing, tvNLike, tvLikes) = createRefs()
        val nameTextColor = Color.Black
        val emailTextColor = Color.Gray
        Text(
            name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = nameTextColor
            ),
            modifier = Modifier.constrainAs(tvName){
                top.linkTo(imgIcon.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            email,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = emailTextColor
            ),
            modifier = Modifier.constrainAs(tvEmail){
                top.linkTo(tvName.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        val numberTextColor = Color.Blue
        Text(
            followerValue.toString(),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = numberTextColor
            ),
            modifier = Modifier.constrainAs(tvNFollower){
                top.linkTo(horizontalGuideLine40Bot)
                end.linkTo(verticalGuideLine30Start, margin = 10.dp)
            }
        )
        Text(
            followerString,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = nameTextColor
            ),
            modifier = Modifier.constrainAs(tvFollowers){
                top.linkTo(horizontalGuideLine40Bot, margin = 30.dp)
                end.linkTo(verticalGuideLine30Start)
            }
        )
        Text(
            followingValue.toString(),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = numberTextColor
            ),
            modifier = Modifier.constrainAs(tvNFollowing){
                top.linkTo(horizontalGuideLine40Bot)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            followingString,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = nameTextColor
            ),
            modifier = Modifier.constrainAs(tvFollowing){
                start.linkTo(parent.start)
                top.linkTo(horizontalGuideLine40Bot, margin = 30.dp)
                end.linkTo(parent.end)
            }
        )
        Text(
            likeValue.toString(),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = numberTextColor
            ),
            modifier = Modifier.constrainAs(tvNLike){
                top.linkTo(horizontalGuideLine40Bot)
                start.linkTo(verticalGuideLine30End, margin = 10.dp)
            }
        )
        Text(
            likeString,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = nameTextColor
            ),
            modifier = Modifier.constrainAs(tvLikes){
                top.linkTo(horizontalGuideLine40Bot, margin = 30.dp)
                start.linkTo(verticalGuideLine30End, margin = 10.dp)
            }
        )
        val (btProf, btAct, btSchel) = createRefs()
        Button(onClick = {},
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(110.dp)
                .constrainAs(btProf){
                bottom.linkTo(parent.bottom, margin = 30.dp)
                start.linkTo(parent.start, margin = 20.dp)
            }) {
            Text(buttonProf,
                modifier=Modifier.padding(0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
            )
        }
        Button(onClick = {},
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(110.dp)
                .constrainAs(btAct){
                    bottom.linkTo(parent.bottom, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Text(buttonActivity,
                modifier=Modifier.padding(0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
            )
        }
        Button(onClick = {},
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(110.dp)
                .constrainAs(btSchel){
                    bottom.linkTo(parent.bottom, margin = 30.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                }) {
            Text(buttonSchedule,
                modifier=Modifier.padding(0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
            )
        }
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    HealthCareSystemTheme {
        ProfileScreen()
    }
}