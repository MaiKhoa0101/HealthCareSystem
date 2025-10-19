package com.parkingSystem.parkingSystem.skeleton

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .background(
                Color.LightGray.copy(alpha = alpha),
                RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun UserInfoSkeleton() {
    ConstraintLayout(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF),
                        Color(0xFF00C5CB)
                    )
                )
            )
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val (
            backButton, moreButton, avatarPlaceholder,
            titleLine, nameLine, statRow1, statRow2, statRow3
        ) = createRefs()

        // Back button skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 30.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                }
        )

        // More button skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .constrainAs(moreButton) {
                    top.linkTo(parent.top, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                }
        )

        // Avatar skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .constrainAs(avatarPlaceholder) {
                    top.linkTo(parent.top, margin = 45.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Title line skeleton
        ShimmerEffect(
            modifier = Modifier
                .height(28.dp)
                .width(80.dp)
                .constrainAs(titleLine) {
                    top.linkTo(avatarPlaceholder.bottom, margin = 15.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Name line skeleton
        ShimmerEffect(
            modifier = Modifier
                .height(24.dp)
                .width(160.dp)
                .constrainAs(nameLine) {
                    top.linkTo(titleLine.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Stats row skeletons
        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine20Bot = createGuidelineFromBottom(0.2f)

        // Experience stat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(statRow1) {
                top.linkTo(horizontalGuideLine20Bot)
                end.linkTo(verticalGuideLine30Start)
            }
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(30.dp)
                    .width(60.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(80.dp)
            )
        }

        // Patients stat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(statRow2) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(30.dp)
                    .width(50.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(70.dp)
            )
        }

        // Ratings stat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(statRow3) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(verticalGuideLine30End, margin = 20.dp)
            }
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(30.dp)
                    .width(45.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(65.dp)
            )
        }
    }
}


@Composable
fun RatingOverviewSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        // Rating header section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main rating score
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ShimmerEffect(
                        modifier = Modifier
                            .width(100.dp)
                            .height(48.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Total reviews count
                ShimmerEffect(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Filter buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Tất cả" button
            ShimmerEffect(
                modifier = Modifier
                    .height(40.dp)
                    .width(70.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            // Star filter buttons
            repeat(5) { index ->
                ShimmerEffect(
                    modifier = Modifier
                        .height(40.dp)
                        .width(50.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Review items
        repeat(4) { index ->
            ReviewItemSkeleton()
            if (index < 3) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ReviewItemSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User profile section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // User name
                    ShimmerEffect(
                        modifier = Modifier
                            .width(140.dp)
                            .height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Date
                    ShimmerEffect(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                    )
                }

                // Menu button
                ShimmerEffect(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating stars
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) {
                    ShimmerEffect(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comment text lines
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                )
            }
        }
    }
}

@Composable
fun PostSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Post header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    ShimmerEffect(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                    )
                }

                ShimmerEffect(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post content
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post image placeholder
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                repeat(3) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShimmerEffect(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ShimmerEffect(
                            modifier = Modifier
                                .width(40.dp)
                                .height(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SkeletonReviewItem() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        val (avatar, name, time, stars, comment, menuIcon) = createRefs()

        // Skeleton Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.2f))
                .constrainAs(avatar) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        )

        // Skeleton Name
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.2f))
                .constrainAs(name) {
                    start.linkTo(avatar.end, margin = 12.dp)
                    top.linkTo(avatar.top)
                }
        )

        // Skeleton Time
        Box(
            modifier = Modifier
                .size(width = 60.dp, height = 13.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.2f))
                .constrainAs(time) {
                    end.linkTo(parent.end)
                    top.linkTo(name.top)
                    bottom.linkTo(name.bottom)
                }
        )

        // Skeleton Stars
        Row(
            modifier = Modifier.constrainAs(stars) {
                top.linkTo(name.bottom, margin = 4.dp)
                start.linkTo(name.start)
            }
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .padding(2.dp)
                )
            }
        }

        // Skeleton Comment
        Column(
            modifier = Modifier.constrainAs(comment) {
                top.linkTo(avatar.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(menuIcon.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
            )
        }

        // Skeleton Menu Icon
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.2f))
                .constrainAs(menuIcon) {
                    top.linkTo(comment.top)
                    end.linkTo(parent.end)
                }
        )
    }
}