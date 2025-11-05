package com.hellodoc.healthcaresystem.presentation.view.user.home.confirm

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hellodoc.healthcaresystem.presentation.viewmodel.PostViewModel

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
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
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
                    color = MaterialTheme.colorScheme.onBackground,
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
                        containerColor = MaterialTheme.colorScheme.error, // đỏ nhạt
                        contentColor = MaterialTheme.colorScheme.background // màu chữ
                    )
                ) {
                    Text("Xóa")
                }

            }
        }
    }
}