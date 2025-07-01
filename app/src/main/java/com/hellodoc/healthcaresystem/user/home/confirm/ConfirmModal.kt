package com.hellodoc.healthcaresystem.user.home.confirm

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel

@Composable
fun ConfirmDeletePostModal(
    sharedPreferences: SharedPreferences,
    postId: String,
    postViewModel: PostViewModel,
    onClickShowConfirmDeleteDialog: () -> Unit,
) {

    Dialog(onDismissRequest = { onClickShowConfirmDeleteDialog() }) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.Gray)
                .padding(16.dp)
        ) {
            Text("Báo cáo người dùng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Bạn có chắc chắn muốn xóa bài viết này không", fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Huỷ",
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable { onClickShowConfirmDeleteDialog() }
                        .padding(8.dp),
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = {
                        onClickShowConfirmDeleteDialog()
                        postViewModel.deletePost(postId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red, // đỏ nhạt
                        contentColor = Color.White // màu chữ
                    )
                ) {
                    Text("Xóa")
                }

            }
        }
    }
}