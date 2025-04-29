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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.responsemodel.modeluser.Rating
import com.hellodoc.healthcaresystem.responsemodel.modeluser.Review
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ReviewResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun ViewRating(
    doctorId: String,
    refreshTrigger: Boolean,
    onEditReview: (reviewId: String, rating: Int, comment: String) -> Unit
) {
    val reviews = remember { mutableStateOf<List<ReviewResponse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    var selectedRating by remember { mutableStateOf(0) }

    LaunchedEffect(doctorId, refreshTrigger) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.reviewService.getReviewsByDoctor(doctorId)
                if (response.isSuccessful) {
                    reviews.value = response.body()?.sortedByDescending { it.createdAt } ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val averageRating = if (reviews.value.isNotEmpty()) {
        reviews.value.mapNotNull { it.rating }.average()
    } else 0.0

    val filteredReviews = if (selectedRating == 0) {
        reviews.value
    } else {
        reviews.value.filter { it.rating == selectedRating }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        RatingSummary(
            average = averageRating,
            count = reviews.value.size,
            selectedRating = selectedRating,
            onRatingSelected = { selectedRating = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize(),
//                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredReviews.forEach { review ->
                ReviewItem(review = review, onEditClick = { id, rating, comment ->
                    onEditReview(id, rating, comment)
                })
            }
        }

    }
}

@Composable
fun RatingSummary(
    average: Double,
    count: Int,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (tvAvg, tvMax, tvCount, ratingBoxes) = createRefs()

        Text(
            text = String.format("%.1f", average),
            style = MaterialTheme.typography.headlineLarge.copy(color = Color(0xFF242760)),
            modifier = Modifier.constrainAs(tvAvg) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )

        Text(
            text = "/5",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            modifier = Modifier.constrainAs(tvMax) {
                baseline.linkTo(tvAvg.baseline)
                start.linkTo(tvAvg.end, margin = 4.dp)
            }
        )

        Text(
            text = "$count đánh giá",
            style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray),
            modifier = Modifier.constrainAs(tvCount) {
                top.linkTo(tvAvg.bottom)
                start.linkTo(tvAvg.start)
            }
        )

        Row(
            modifier = Modifier.constrainAs(ratingBoxes) {
                top.linkTo(parent.top)
                start.linkTo(tvCount.end, margin = 15.dp)
                end.linkTo(parent.end)
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 5 downTo 1) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedRating == i) Color(0xFFDADADA) else Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { onRatingSelected(i) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "$i", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "★")
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: ReviewResponse,
    onEditClick: (reviewId: String, rating: Int, comment: String) -> Unit = { _, _, _ -> }
) {
    var showMenu by remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        val (avatar, name, time, stars, comment, menuIcon) = createRefs()

        Image(

            painter = rememberAsyncImagePainter(review.user?.userImage ?: ""),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .constrainAs(avatar) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
            contentScale = ContentScale.Crop
        )

        Text(
            text = review.user?.name ?: "Ẩn danh",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(name) {
                start.linkTo(avatar.end, margin = 12.dp)
                top.linkTo(avatar.top)
            }
        )

        Text(
            text = review.createdAt?.substring(0, 10) ?: "",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.constrainAs(time) {
                end.linkTo(parent.end)
                top.linkTo(name.top)
                bottom.linkTo(name.bottom)
            }
        )

        Row(
            modifier = Modifier.constrainAs(stars) {
                top.linkTo(name.bottom, margin = 4.dp)
                start.linkTo(name.start)
            }
        ) {
            repeat(review.rating ?: 0) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = review.comment ?: "Không có nội dung",
            fontSize = 14.sp,
            modifier = Modifier.constrainAs(comment) {
                top.linkTo(avatar.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(menuIcon.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        Box(
            modifier = Modifier.constrainAs(menuIcon) {
                top.linkTo(comment.top)
                end.linkTo(parent.end)
            }
        ) {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset((-16).dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Xóa")
                        }
                    },
                    onClick = { showMenu = false }
                )
                Divider(thickness = 1.dp, color = Color.LightGray)
                DropdownMenuItem(
                    text = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Sửa") }},
                    onClick = {
                        showMenu = false
                        review.id?.let { id ->
                            onEditClick(id, review.rating ?: 5, review.comment ?: "")
                        }
                    }
                )
            }
        }
    }
}
