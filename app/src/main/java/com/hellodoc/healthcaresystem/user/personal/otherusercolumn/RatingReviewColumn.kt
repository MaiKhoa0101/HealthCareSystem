package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.user.personal.model.Rating
import com.hellodoc.healthcaresystem.user.personal.model.Review
import com.hellodoc.healthcaresystem.R

@Composable
fun ViewRating(){
    val reviews = listOf(
        Review(
            userName = "Bành Thị Tỏng",
            userAvatar = R.drawable.avarta,
            ratingStars = 5,
            reviewText = "Bác sĩ rất tận tâm và chuyên nghiệp. Rất hài lòng với dịch vụ!",
            timeAgo = "1 giờ trước"
        ),
        Review(
            userName = "Trần Văn Tèo",
            userAvatar = R.drawable.avarta,
            ratingStars = 4,
            reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
            timeAgo = "2 giờ trước"
        ),
        Review(
            userName = "Trần Văn Tèo",
            userAvatar = R.drawable.avarta,
            ratingStars = 4,
            reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
            timeAgo = "1 giờ trước"
        ),
        Review(
            userName = "Trần Văn Tèo",
            userAvatar = R.drawable.avarta,
            ratingStars = 4,
            reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
            timeAgo = "1 giờ trước"
        ),
        Review(
            userName = "Trần Văn Tèo",
            userAvatar = R.drawable.avarta,
            ratingStars = 4,
            reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
            timeAgo = "1 giờ trước"
        ),
        Review(
            userName = "Trần Văn Tèo",
            userAvatar = R.drawable.avarta,
            ratingStars = 4,
            reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
            timeAgo = "1 giờ trước"
        )
    )
    AverageRating(
        rating = Rating(
            number = 4.9,
            sum = 10
        ),
        reviews = reviews
    )
}

@Composable
fun AverageRating(
    rating: Rating,
    reviews: List<Review>,
    modifier: Modifier = Modifier
) {
    var selectedRating by remember { mutableStateOf(5) }

    val filteredReviews = remember(selectedRating, reviews) {
        reviews.filter { it.ratingStars == selectedRating }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 66.dp)
//                .verticalScroll(rememberScrollState())
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (tvNRating, tv5, tvSumRating, ratingBoxes) = createRefs()

                Text(
                    text = rating.number.toString(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvNRating) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text = "/5",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.constrainAs(tv5) {
                        baseline.linkTo(tvNRating.baseline)
                        start.linkTo(tvNRating.end, margin = 4.dp)
                    }
                )

                Text(
                    text = "${rating.sum} đánh giá",
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                    modifier = Modifier.constrainAs(tvSumRating) {
                        top.linkTo(tvNRating.bottom, margin = 4.dp)
                        start.linkTo(tvNRating.start)
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.constrainAs(ratingBoxes) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(tvSumRating.end, margin = 15.dp)
                        end.linkTo(parent.end)
                    }
                ) {
                    for (i in 5 downTo 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedRating == i) Color(0xFFDADADA) else Color.White
                                )
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable { selectedRating = i }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$i",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("★", color = Color.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredReviews.forEach { review ->
                    ReviewItem(review = review)
                }
            }
        }

//        Button(
//            onClick = {},
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.Black,
//                contentColor = Color.White
//            ),
//            shape = RoundedCornerShape(12.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .height(50.dp)
//        ) {
//            Text("Viết đánh giá", fontSize = 16.sp)
//        }
    }
}


@Composable
fun ReviewItem(review: Review, modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        val (avatarRef, nameRef, timeRef, starsRef, textRef, menuIconRef) = createRefs()
        Image(
            painter = painterResource(id = review.userAvatar),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            contentScale = ContentScale.Crop
        )
        Text(
            text = review.userName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 12.dp)
            }
        )
        Text(
            text = review.timeAgo,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.constrainAs(timeRef) {
                top.linkTo(nameRef.top)
                bottom.linkTo(nameRef.bottom)
                end.linkTo(parent.end)
            }
        )
        Row(
            modifier = Modifier.constrainAs(starsRef) {
                top.linkTo(nameRef.bottom, margin = 4.dp)
                start.linkTo(nameRef.start)
            }
        ) {
            repeat(review.ratingStars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = review.reviewText,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.constrainAs(textRef) {
                top.linkTo(avatarRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(menuIconRef.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )
        Box(modifier = Modifier.constrainAs(menuIconRef) {
            end.linkTo(parent.end)
            top.linkTo(textRef.top)
        }) {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(x = (-16).dp, y = 0.dp),
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            ) {
                DropdownMenuItem(
                    text = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Tố cáo", textAlign = TextAlign.Center)
                        }
                    },
                    onClick = {
                        showMenu = false
                    }
                )
                Divider()
                DropdownMenuItem(
                    text = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Ẩn", textAlign = TextAlign.Center)
                        }
                    },
                    onClick = {
                        showMenu = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    HealthCareSystemTheme {
        val reviews = listOf(
            Review(
                userName = "Bành Thị Tỏng",
                userAvatar = R.drawable.avarta,
                ratingStars = 5,
                reviewText = "Bác sĩ rất tận tâm và chuyên nghiệp. Rất hài lòng với dịch vụ!",
                timeAgo = "1 giờ trước"
            ),
            Review(
                userName = "Trần Văn Tèo",
                userAvatar = R.drawable.avarta,
                ratingStars = 4,
                reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
                timeAgo = "2 giờ trước"
            ),
            Review(
                userName = "Trần Văn Tèo",
                userAvatar = R.drawable.avarta,
                ratingStars = 4,
                reviewText = "Dịch vụ ổn, nhưng đợi hơi lâu.",
                timeAgo = "1 giờ trước"
            )
        )

        AverageRating(
            rating = Rating(
                number = 4.9,
                sum = 10
            ),
            reviews = reviews
        )
    }
}
