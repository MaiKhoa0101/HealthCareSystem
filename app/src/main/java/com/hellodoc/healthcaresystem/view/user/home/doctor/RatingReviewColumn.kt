package com.hellodoc.healthcaresystem.view.user.home.doctor

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReviewResponse
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.skeleton.SkeletonReviewItem
import com.hellodoc.healthcaresystem.viewmodel.ReviewViewModel
import kotlinx.coroutines.launch

@Composable
fun ViewRating(
    doctorId: String,
    refreshTrigger: Boolean,
    onEditReview: (reviewId: String, rating: Int, comment: String) -> Unit,
    onDeleteReview: () -> Unit,
    userId: String
) {
    var selectedRating by remember { mutableStateOf(0) }

    val reviewViewModel: ReviewViewModel= hiltViewModel()
    val reviews by reviewViewModel.reviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    LaunchedEffect(doctorId, refreshTrigger) {
        reviewViewModel.getReviewsByDoctor(doctorId)
    }

    val averageRating = if (reviews.isNotEmpty()) {
        reviews.mapNotNull { it.rating }.average()
    } else 0.0

    val filteredReviews = if (selectedRating == 0) {
        reviews
    } else {
        reviews.filter { it.rating == selectedRating }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        RatingSummary(
            average = averageRating,
            count = reviews.size,
            selectedRating = selectedRating,
            onRatingSelected = { selectedRating = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                repeat(3) { // Show 3 skeleton items while loading
                    SkeletonReviewItem()
                }
            } else {
                filteredReviews.forEach { review ->
                    ReviewItem(
                        review = review,
                        onEditClick = { id, rating, comment ->
                            onEditReview(id, rating, comment)
                        },
                        onDeleteClick = {
                            onDeleteReview()
                        },
                        currentUserId = userId,
                    )
                }
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
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.constrainAs(tvAvg) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )

        Text(
            text = "/5",
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.constrainAs(tvMax) {
                baseline.linkTo(tvAvg.baseline)
                start.linkTo(tvAvg.end, margin = 4.dp)
            }
        )

        Text(
            text = "$count đánh giá",
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground),
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
                        .background(if (selectedRating == i) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
                        .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(8.dp))
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
    onEditClick: (reviewId: String, rating: Int, comment: String) -> Unit = { _, _, _ -> },
    onDeleteClick: (String) -> Unit = {},
    currentUserId: String
) {
    var showMenu by remember { mutableStateOf(false) }
    val canEditOrDelete = currentUserId == review.user?.id
    val reviewViewModel: ReviewViewModel = hiltViewModel()
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
            color = MaterialTheme.colorScheme.onBackground,
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
                    tint = MaterialTheme.colorScheme.outlineVariant,
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

        if(canEditOrDelete) {
            Box(
                modifier = Modifier.constrainAs(menuIcon) {
                    bottom.linkTo(comment.top, margin = (-8).dp)
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
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Xóa")
                            }
                        },
                        onClick = {
                            showMenu = false
                            review.id?.let { id ->
                                reviewViewModel.deleteReview(id, onDeleteClick)
                            }
                        }
                    )
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiaryContainer)
                    DropdownMenuItem(
                        text = {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) { Text("Sửa") }
                        },
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
}