package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hellodoc.healthcaresystem.requestmodel.ReviewRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateReviewRequest
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    doctorId: String,
    userId: String,
    initialRating: Int? = null,
    initialComment: String? = null,
    reviewId: String? = null,
    onBackClick: () -> Unit = {},
    onSubmitClick: (Int, String) -> Unit = { _, _ -> }
) {
    var selectedStar by remember { mutableStateOf(initialRating ?: 5) }
    var commentText by remember { mutableStateOf(initialComment ?: "") }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (backRef, titleRef, ratingLabelRef, starRowRef, commentLabelRef, textFieldRef, buttonRef) = createRefs()

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.constrainAs(backRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Viết đánh giá",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(backRef.top)
                    start.linkTo(backRef.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(backRef.bottom)
                    centerHorizontallyTo(parent)
                }
            )

            Text(
                text = "Đánh giá của bạn:",
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier.constrainAs(ratingLabelRef) {
                    top.linkTo(titleRef.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.constrainAs(starRowRef) {
                    top.linkTo(ratingLabelRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                }
            ) {
                for (i in 5 downTo 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedStar == i) Color(0xFFDADADA) else Color.White
                            )
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable { selectedStar = i }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$i", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("★")
                        }
                    }
                }
            }

            Text(
                text = "Viết nhận xét của bạn:",
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier.constrainAs(commentLabelRef) {
                    top.linkTo(starRowRef.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                }
            )

            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Nhập đánh giá của bạn...") },
                modifier = Modifier
                    .constrainAs(textFieldRef) {
                        top.linkTo(commentLabelRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .height(120.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            if (reviewId == null) {
                                // Tạo mới
                                val reviewRequest = ReviewRequest(
                                    userId = userId,
                                    doctorId = doctorId,
                                    rating = selectedStar,
                                    comment = commentText
                                )
                                val response = RetrofitInstance.reviewService.createReview(reviewRequest)
                                if (response.isSuccessful && response.body() != null) {
                                    onSubmitClick(selectedStar, commentText)
                                }
                            } else {
                                // Sửa review
                                val updateRequest = UpdateReviewRequest(
                                    rating = selectedStar,
                                    comment = commentText
                                )
                                val response = RetrofitInstance.reviewService.updateReview(reviewId, updateRequest)
                                if (response.isSuccessful) {
                                    onSubmitClick(selectedStar, commentText)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .constrainAs(buttonRef) {
                        top.linkTo(textFieldRef.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Đăng", fontSize = 20.sp)
            }
        }
    }
}
//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun WriteReviewPreview() {
//    WriteReviewScreen()
//}
