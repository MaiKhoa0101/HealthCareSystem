package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.personal.userModel
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import com.google.accompanist.pager.*
import com.hellodoc.healthcaresystem.user.home.HomeActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenCommentUI(
    navHostController: NavHostController,
    postId: String,
    onClose: () -> Unit,
    postViewModel: PostViewModel,
    currentUserId: String
) {
    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()
    var newComment by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var commentIndex by remember { mutableStateOf(6) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    BackHandler {
        onClose() //Nút lùi về của màn hình
    }

    LaunchedEffect(postId) {
        postViewModel.fetchComments(postId)
        sheetState.expand()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.92f) // Chiều cao mong muốn (85% màn hình)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Close",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text("Bình luận", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(comments.take(commentIndex)) { comment ->
                    Row(verticalAlignment = Alignment.Top) {
                        AsyncImage(
                            model = comment.user?.avatarURL ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (currentUserId != comment.user?.id) {
                                        navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                            set("UserId", comment.user?.id)
                                        }
                                        navHostController.navigate("otherUserProfile")
                                    } else {
                                        navHostController.navigate("personal")
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(comment.user?.name ?: "Ẩn danh", fontWeight = FontWeight.Bold)
                            Text(comment.content)
                        }
                        Box {
                            IconButton(onClick = { activeMenuCommentId = comment.id }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = activeMenuCommentId == comment.id,
                                onDismissRequest = { activeMenuCommentId = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Xóa") },
                                    onClick = {
                                        activeMenuCommentId = null
                                        coroutineScope.launch {
                                            postViewModel.deleteComment(comment.id, postId)
                                            postViewModel.fetchComments(postId)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sửa") },
                                    onClick = {
                                        editingCommentId = comment.id
                                        editedCommentContent = comment.content
                                        activeMenuCommentId = null
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (commentIndex < comments.size) {
                    item {
                        Text(
                            text = "Xem thêm...",
                            modifier = Modifier
                                .clickable { commentIndex += 6 }
                                .padding(8.dp),
                            color = Color.Blue
                        )
                    }
                }
            }

            // Nhập bình luận
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = if (editingCommentId != null) editedCommentContent else newComment,
                    onValueChange = {
                        if (editingCommentId != null) editedCommentContent = it else newComment = it
                    },
                    placeholder = { Text("Nhập bình luận...") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    coroutineScope.launch {
                        if (editingCommentId != null) {
                            postViewModel.updateComment(editingCommentId!!, currentUserId, userModel, editedCommentContent)
                            editingCommentId = null
                            editedCommentContent = ""
                        } else {
                            postViewModel.sendComment(postId, currentUserId, userModel, newComment)
                            newComment = ""
                        }
                        postViewModel.fetchComments(postId)
                    }
                }) {
                    Text(if (editingCommentId != null) "Lưu" else "Gửi")
                }
            }
        }
    }
}
