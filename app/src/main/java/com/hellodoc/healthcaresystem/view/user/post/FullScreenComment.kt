package com.hellodoc.healthcaresystem.view.user.post

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CommentPostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetNewsCommentResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
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
    currentUser: User
) {
    val uiState = rememberCommentUIState(postViewModel, postId)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope() // ✅ Thêm dòng này

    BackHandler { onClose() }

    // Load more comments when reaching end of list
    LaunchedEffect(uiState.commentIndex, uiState.hasMore) {
        observeScrollToLoadMore(
            listState = uiState.listState,
            hasMore = uiState.hasMore,
            isLoading = uiState.isLoadingMore,
            onLoadMore = {
                coroutineScope.launch {
                    uiState.loadMoreComments(postViewModel, postId)
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.92f),
        dragHandle = { CommentSheetDragHandle() }
    ) {
        CommentScreenContent(
            uiState = uiState,
            postViewModel = postViewModel,
            postId = postId,
            currentUser = currentUser,
            navHostController = navHostController
        )
    }
}

@Composable
internal fun rememberCommentUIState(
    postViewModel: PostViewModel,
    postId: String
): CommentUIState {
    val commentsMap by postViewModel.commentsMap.collectAsState()
    val hasMoreMap by postViewModel.hasMoreMap.collectAsState()

    return remember {
        CommentUIState()
    }.apply {
        comments = commentsMap[postId] ?: emptyList()
        hasMore = hasMoreMap[postId] ?: true
    }
}

@Composable
private fun CommentSheetDragHandle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Icon(
            painter = painterResource(id = R.drawable.arrowdown),
            contentDescription = "Kéo xuống để tắt",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
internal fun CommentScreenContent(
    uiState: CommentUIState,
    postViewModel: PostViewModel,
    postId: String,
    currentUser: User,
    navHostController: NavHostController
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        CommentInput(
            uiState = uiState,
            postViewModel = postViewModel,
            postId = postId,
            currentUser = currentUser,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(20.dp))

        CommentList(
            uiState = uiState,
            postViewModel = postViewModel,
            postId = postId,
            currentUser = currentUser,
            navHostController = navHostController,
        )


    }
}



@Composable
fun CommentList(
    uiState: CommentUIState,
    postViewModel: PostViewModel,
    postId: String,
    currentUser: User,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        state = uiState.listState
    ) {
        items(uiState.comments) { comment ->
            CommentItem(
                comment = comment,
                postId = postId,
                postViewModel = postViewModel,
                currentUser = currentUser,
                navHostController = navHostController,
                uiState = uiState
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            CommentListFooter(
                isLoadingMore = uiState.isLoadingMore,
                hasMore = uiState.hasMore
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentPostResponse,
    postId: String,
    postViewModel: PostViewModel,
    currentUser: User,
    navHostController: NavHostController,
    uiState: CommentUIState
) {

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(10.dp)
        ) {
            CommentUserAvatar(
                user = comment.user,
                currentUser = currentUser,
                navHostController = navHostController
            )

            Spacer(modifier = Modifier.width(8.dp))

            CommentContent(
                comment = comment,
                postId = postId,
                postViewModel = postViewModel,
                uiState = uiState
            )
        }

}

@Composable
private fun CommentUserAvatar(
    user: CommentPostResponse.User?,
    currentUser: User,
    navHostController: NavHostController
) {
    AsyncImage(
        model = user?.avatarURL ?: "",
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .border( 1.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .clip(CircleShape)
            .clickable {
                navigateToUserProfile(
                    user = user,
                    currentUser = currentUser,
                    navHostController = navHostController
                )
            }
    )
}

@Composable
private fun CommentContent(
    comment: CommentPostResponse,
    postId: String,
    postViewModel: PostViewModel,
    uiState: CommentUIState
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.user?.name ?: "Ẩn danh",
                fontWeight = FontWeight.Bold
            )
            Text(text = comment.content)
        }

        ReportCommentFunction(
            comment = comment,
            postId = postId,
            postViewModel = postViewModel,
            setEditingCommentId = { uiState.editingCommentId = it },
            setEditedCommentContent = { uiState.editedCommentContent = it },
            activeMenuCommentId = uiState.activeMenuCommentId,
            setActiveMenuCommentId = { uiState.activeMenuCommentId = it }
        )
    }
}

@Composable
fun CommentListFooter(
    isLoadingMore: Boolean,
    hasMore: Boolean
) {
    if (isLoadingMore && hasMore) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Đã hết bình luận")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommentInput(
    uiState: CommentUIState,
    postViewModel: PostViewModel,
    postId: String,
    currentUser: User,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        TextField(
            value = if (uiState.editingCommentId != null) {
                uiState.editedCommentContent
            } else {
                uiState.newComment
            },
            onValueChange = { value ->
                if (uiState.editingCommentId != null) {
                    uiState.editedCommentContent = value
                } else {
                    uiState.newComment = value
                }
            },
            placeholder = {
                Text("Nhập bình luận...", color = MaterialTheme.colorScheme.onBackground) // Màu placeholder
            },
            modifier = Modifier.weight(1f).border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.background,               // Màu nền ô nhập
                focusedTextColor = MaterialTheme.colorScheme.onBackground,                   // Màu chữ khi focus
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,                 // Màu chữ khi không focus
                focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,             // Màu placeholder khi focus
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,           // Màu placeholder khi không focus
                focusedIndicatorColor = Color.Transparent,        // Xóa viền khi focus
                unfocusedIndicatorColor = Color.Transparent       // Xóa viền khi không focus
            )
        )

        val userModel = currentUser.role
        val currentUserId = currentUser.id

        Button(
            enabled = uiState.newComment.isNotBlank() || uiState.editedCommentContent.isNotBlank(),
            modifier = Modifier
                .padding(start = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            onClick = {
                coroutineScope.launch {
                    uiState.submitComment(
                        postViewModel = postViewModel,
                        postId = postId,
                        currentUserId = currentUserId,
                        userModel = userModel
                    )
                }
            }
        ) {
            Text(if (uiState.editingCommentId != null) "Lưu" else "Gửi")
        }
    }
}

// UI State class
@Stable
class CommentUIState {
    var comments by mutableStateOf<List<CommentPostResponse>>(emptyList())
    var hasMore by mutableStateOf(true)
    var newComment by mutableStateOf("")
    var editingCommentId by mutableStateOf<String?>(null)
    var editedCommentContent by mutableStateOf("")
    var activeMenuCommentId by mutableStateOf<String?>(null)
    var commentIndex by mutableStateOf(0)
    var isLoadingMore by mutableStateOf(false)
    val listState = LazyListState()

    suspend fun loadMoreComments(postViewModel: PostViewModel, postId: String) {
        if (hasMore && !isLoadingMore) {
            isLoadingMore = true
            postViewModel.fetchComments(
                postId = postId,
                skip = commentIndex,
                limit = 10,
                append = true
            )
            println("Comment index la: $commentIndex")
            commentIndex += 10
            isLoadingMore = false
        }
    }

    suspend fun submitComment(
        postViewModel: PostViewModel,
        postId: String,
        currentUserId: String,
        userModel: String = ""
    ) {
        if (editingCommentId != null) {
            postViewModel.updateComment(
                editingCommentId!!,
                currentUserId,
                userModel,
                editedCommentContent
            )
            editingCommentId = null
            editedCommentContent = ""
        } else {
            postViewModel.sendComment(postId, currentUserId, userModel, newComment)
            newComment = ""
        }

        delay(200) // Wait for data to load
        listState.animateScrollToItem(0)
        commentIndex = 10
        println("Comment index sau khi gui la: $commentIndex")
    }
}

// Helper functions
private fun navigateToUserProfile(
    user: CommentPostResponse.User?,
    currentUser: User,
    navHostController: NavHostController
) {
    if (currentUser.id != user?.id) {
        navHostController.currentBackStackEntry?.savedStateHandle?.apply {
            set("UserId", user?.id)
        }
        navHostController.navigate("otherUserProfile")
    } else {
        navHostController.navigate("personal")
    }
}

internal suspend fun observeScrollToLoadMore(
    listState: LazyListState,
    hasMore: Boolean,
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    snapshotFlow {
        val layoutInfo = listState.layoutInfo
        val totalItems = layoutInfo.totalItemsCount
        val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        lastVisibleItemIndex >= totalItems - 1
    }.distinctUntilChanged()
        .collect { isAtEnd ->
            if (isAtEnd && hasMore && !isLoading) {
                onLoadMore()
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
                            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                    placeholder = {
                        Text("Nhập bình luận...", color = MaterialTheme.colorScheme.onBackground) // Màu placeholder
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.background,               // Màu nền ô nhập
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,                   // Màu chữ khi focus
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,                 // Màu chữ khi không focus
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,             // Màu placeholder khi focus
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,           // Màu placeholder khi không focus
                        focusedIndicatorColor = Color.Transparent,        // Xóa viền khi focus
                        unfocusedIndicatorColor = Color.Transparent       // Xóa viền khi không focus
                    )
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
    comment: CommentPostResponse,
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

