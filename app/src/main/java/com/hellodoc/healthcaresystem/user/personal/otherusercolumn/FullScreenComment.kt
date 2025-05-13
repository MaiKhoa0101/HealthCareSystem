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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.responsemodel.GetCommentPostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetNewsCommentResponse
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

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
    val comments = (commentsMap[postId] ?: emptyList())
    val hasMoreMap by postViewModel.hasMoreMap.collectAsState()
    val hasMore = hasMoreMap[postId] ?: true

    var newComment by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var commentIndex by remember { mutableStateOf(0) }
    var isLoadingMore by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    BackHandler { onClose() }

    LaunchedEffect(hasMore) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= totalItems - 1
        }.distinctUntilChanged()
            .collect { isAtEnd ->
                if (isAtEnd && hasMore && !isLoadingMore) {
                    isLoadingMore = true
                    postViewModel.fetchComments(
                        postId = postId,
                        skip = commentIndex,
                        limit = 10,
                        append = true
                    )
                    commentIndex+=10
                    isLoadingMore = false
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.92f),
        dragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(15.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrowdown),
                    contentDescription = "Kéo xuống để tắt",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(comments) { comment ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray)
                            .padding(10.dp)
                    ) {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(comment.user?.name ?: "Ẩn danh", fontWeight = FontWeight.Bold)
                                Text(comment.content)
                            }
                            ReportCommentFunction(
                                comment = comment,
                                postId = postId,
                                postViewModel = postViewModel,
                                setEditingCommentId = { editingCommentId = it },
                                setEditedCommentContent = { editedCommentContent = it },
                                activeMenuCommentId = activeMenuCommentId,
                                setActiveMenuCommentId = { activeMenuCommentId = it }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (isLoadingMore && hasMore) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        }
                    }
                    println("isLoadingMore "+isLoadingMore +" commentIndex "+ commentIndex + " comments.size "+comments.size)
                }
                else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                            ) {
                            Text("Đã hết bình luận")
                        }

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
                        commentIndex=0
                        postViewModel.fetchComments(postId, skip = commentIndex, limit = 10, append = false)
                        delay(200) // Đợi dữ liệu load xong, rồi mới scroll
                        commentIndex+=10
                        listState.animateScrollToItem(0)
                    }

                }) {
                    Text(if (editingCommentId != null) "Lưu" else "Gửi")
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenCommentNews(
    navHostController: NavHostController,
    newsId: String,
    onClose: () -> Unit,
    newsViewModel: NewsViewModel,
    currentUserId: String,
    currentUserModel: String
) {
    val commentsMap by newsViewModel.newsComments.collectAsState()
    val comments = commentsMap[newsId] ?: emptyList()

    var newComment by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler { onClose() }

    LaunchedEffect(newsId) {
        newsViewModel.getComments(newsId)
        sheetState.expand()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.92f),
        dragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(15.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrowdown),
                    contentDescription = "Kéo xuống để tắt",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(comments) { comment ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray)
                            .padding(10.dp)
                    ) {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(comment.user?.name ?: "Ẩn danh", fontWeight = FontWeight.Bold)
                                Text(comment.content)
                            }
                            ReportNewsCommentFunction(
                                comment = comment,
                                newsId = newsId,
                                newsViewModel = newsViewModel,
                                setEditingCommentId = { editingCommentId = it },
                                setEditedCommentContent = { editedCommentContent = it },
                                activeMenuCommentId = activeMenuCommentId,
                                setActiveMenuCommentId = { activeMenuCommentId = it }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

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
                            newsViewModel.updateComment(editingCommentId!!, currentUserId, currentUserModel, editedCommentContent)
                            editingCommentId = null
                            editedCommentContent = ""
                        } else {
                            newsViewModel.sendComment(newsId, currentUserId, newComment)
                            newComment = ""
                        }
                        newsViewModel.getComments(newsId)
                        delay(200)
                        listState.animateScrollToItem(0)
                    }
                }) {
                    Text(if (editingCommentId != null) "Lưu" else "Gửi")
                }
            }
        }
    }
}
@Composable
fun ReportNewsCommentFunction(
    comment: GetNewsCommentResponse,
    newsId: String,
    newsViewModel: NewsViewModel,
    setEditingCommentId: (String?) -> Unit,
    setEditedCommentContent: (String) -> Unit,
    activeMenuCommentId: String?,
    setActiveMenuCommentId: (String?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Box {
        IconButton(onClick = { setActiveMenuCommentId(comment.id) }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        DropdownMenu(
            expanded = activeMenuCommentId == comment.id,
            onDismissRequest = { setActiveMenuCommentId(null) }
        ) {
            DropdownMenuItem(
                text = { Text("Xóa") },
                onClick = {
                    setActiveMenuCommentId(null)
                    coroutineScope.launch {
                        newsViewModel.deleteComment(comment.id, newsId)
                        newsViewModel.getComments(newsId)
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Sửa") },
                onClick = {
                    setEditingCommentId(comment.id)
                    setEditedCommentContent(comment.content)
                    setActiveMenuCommentId(null)
                }
            )
        }
    }
}

@Composable
fun ReportCommentFunction(
    comment: GetCommentPostResponse,
    postId:String,
    postViewModel: PostViewModel,
    setEditingCommentId: (String?) -> Unit,
    setEditedCommentContent : (String) -> Unit,
    activeMenuCommentId: String?,
    setActiveMenuCommentId: (String?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Box {
        IconButton(onClick = { setActiveMenuCommentId(comment.id) }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        DropdownMenu(
            expanded = activeMenuCommentId == comment.id,
            onDismissRequest = { setActiveMenuCommentId(null) }
        ) {
            DropdownMenuItem(
                text = { Text("Xóa") },
                onClick = {
                    setActiveMenuCommentId(null)
                    coroutineScope.launch {
                        postViewModel.deleteComment(comment.id, postId)
                        postViewModel.fetchComments(postId)
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Sửa") },
                onClick = {
                    setEditingCommentId (comment.id)
                    setEditedCommentContent(comment.content)
                    setActiveMenuCommentId(null)
                }
            )
        }
    }
}

