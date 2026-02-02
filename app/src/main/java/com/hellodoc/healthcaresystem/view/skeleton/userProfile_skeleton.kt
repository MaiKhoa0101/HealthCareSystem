package com.hellodoc.healthcaresystem.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.blindview.userblind.personal.Setting

@Composable
fun UserSkeleton(
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .padding(10.dp)
    ) {
        // Skeleton for UserIntroSection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { navHostController.navigate("setting") },
                contentAlignment = Alignment.TopEnd
            ) {
                // Settings Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { navHostController.navigate("setting") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settingbtn),
                        contentDescription = "Setting",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                // Skeleton for avatar
                ShimmerEffect(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Skeleton for name
                ShimmerEffect(
                    modifier = Modifier
                        .height(20.dp)
                        .width(160.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Skeleton for email
                ShimmerEffect(
                    modifier = Modifier
                        .height(14.dp)
                        .width(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

//        // Skeleton for UserProfileModifierSection
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp),
//            horizontalArrangement = Arrangement.SpaceAround
//        ) {
//            // Skeleton for "Chỉnh sửa hồ sơ" button
//            ShimmerEffect(
//                modifier = Modifier
//                    .height(60.dp)
//                    .width(128.dp)
//                    .clip(RoundedCornerShape(10.dp))
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            // Skeleton for "Quản lý phòng khám" button
//            ShimmerEffect(
//                modifier = Modifier
//                    .height(60.dp)
//                    .width(128.dp)
//                    .clip(RoundedCornerShape(10.dp))
//            )
//        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun OtherUserSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .padding(10.dp)
    ) {
        // Skeleton for UserIntroSection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                // Skeleton for avatar
                ShimmerEffect(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Skeleton for name
                ShimmerEffect(
                    modifier = Modifier
                        .height(20.dp)
                        .width(160.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Skeleton for email
                ShimmerEffect(
                    modifier = Modifier
                        .height(14.dp)
                        .width(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

//        // Skeleton for UserProfileModifierSection
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp),
//            horizontalArrangement = Arrangement.SpaceAround
//        ) {
//            // Skeleton for "Chỉnh sửa hồ sơ" button
//            ShimmerEffect(
//                modifier = Modifier
//                    .height(60.dp)
//                    .width(128.dp)
//                    .clip(RoundedCornerShape(10.dp))
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            // Skeleton for "Quản lý phòng khám" button
//            ShimmerEffect(
//                modifier = Modifier
//                    .height(60.dp)
//                    .width(128.dp)
//                    .clip(RoundedCornerShape(10.dp))
//            )
//        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
