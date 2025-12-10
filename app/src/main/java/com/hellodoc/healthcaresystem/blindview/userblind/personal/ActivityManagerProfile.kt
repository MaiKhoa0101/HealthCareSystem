package com.hellodoc.healthcaresystem.blindview.userblind.personal


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController


@Composable
fun ActivityManagerScreen(onBack: () -> Unit, navHostController: NavHostController) {
    Column {
        TopBar(
            title = "Nhật ký hoạt động",
            onClick = { navHostController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable { navHostController.navigate("userComment") }
                    .padding(16.dp)
            ) {
                Column {
                    Text("Bình luận & cảm xúc", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Xem lại bài viết bạn đã bình luận hoặc tương tác", fontSize = 14.sp)
                }
            }

            //Đã thích
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp))
                    .clickable { navHostController.navigate("userFavorite") }
                    .padding(16.dp)
            ) {
                Column {
                    Text("Bài viết đã thích", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Danh sách bài viết bạn đã bày tỏ cảm xúc", fontSize = 14.sp)
                }
            }
        }
    }
}

 @Composable
 fun TopBar(title: String,onClick: () -> Unit) {
     Box(
         modifier = Modifier
             .fillMaxWidth()
             .background(MaterialTheme.colorScheme.primaryContainer)
             .height(56.dp)
     ) {
         // Nút quay lại
         Icon(
             imageVector = Icons.Filled.ArrowBack,
             contentDescription = "Back Button",
             tint = MaterialTheme.colorScheme.onBackground,
             modifier = Modifier
                 .align(Alignment.CenterStart)
                 .padding(start = 16.dp)
                 .clickable { onClick() }
         )

         // Tiêu đề ở giữa
         Text(
             text = title,
             color = MaterialTheme.colorScheme.onBackground,
             style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
             modifier = Modifier.align(Alignment.Center)
         )
     }
 }
