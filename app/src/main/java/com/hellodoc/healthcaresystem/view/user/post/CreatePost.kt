package com.hellodoc.healthcaresystem.view.user.post

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.view.user.supportfunction.getType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdatePostRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.view.user.supportfunction.extractOneFrame
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CreatePostScreen(
    context: Context,
    navController: NavHostController,
    postViewModel: PostViewModel= hiltViewModel(),
    postId: String? = null
) {
    val storagePermissionState: PermissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        }
        else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    var showPermissionDialog by remember { mutableStateOf(false) }

    // User info
    val userViewModel: UserViewModel = hiltViewModel()

    val user by userViewModel.user.collectAsState()
    var userId by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    // Post states
    var postText by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var frameUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pairedList by remember { mutableStateOf<List<Pair<Uri, Uri>>>(emptyList()) }

    val maxWords = 400
    val wordCount by remember(postText) {
        derivedStateOf { countWords(postText) }
    }

    // Loading & dialog
    val posts by postViewModel.posts.collectAsState()
    val updateSuccess by postViewModel.updateSuccess.collectAsState()
    val isUpdating by postViewModel.isUpdating.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()
    var showContentEmptyDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                uris.forEach { uri ->
                    val type = getType(uri, context)
                    if (type.contains("video")) {
                        withContext(Dispatchers.IO) {
                            extractOneFrame(context, uri)
                        }?.let { frameUri ->
                            // cập nhật state ở Main thread
                            pairedList = pairedList + (uri to frameUri)
                            selectedImageUris = selectedImageUris + uri
                        }
                    } else {
                        // ảnh thì paired chính nó
                        pairedList = pairedList + (uri to uri)
                        selectedImageUris = selectedImageUris + uri
                    }
                }
            }
        }
    }

    // Load user info & post data
    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttribute("userId", context)
        userRole = userViewModel.getUserAttribute("role", context)
        userViewModel.getUser(userId)

        postId?.let { id -> postViewModel.getPostById(id, context) }
    }

    // Nếu đang edit thì load dữ liệu cũ
    LaunchedEffect(posts) {
        if (postId != null && posts.isNotEmpty()) {
            posts.firstOrNull { it.id == postId }?.let { post ->
                postText = post.content ?: ""
                selectedImageUris = post.media?.mapNotNull { Uri.parse(it) } ?: emptyList()
            }
        }
    }

    // Navigate khi update thành công
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            postViewModel.resetUpdateSuccess()
            navController.currentBackStackEntry?.savedStateHandle?.set("shouldReload", true)
            navController.navigate("home")
        }
    }

    // Handle chọn media
    fun handleMediaSelection() {
        when {
            storagePermissionState.status.isGranted -> {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            }
            storagePermissionState.status.shouldShowRationale -> {
                showPermissionDialog = true
            }
            else -> storagePermissionState.launchPermissionRequest()
        }
    }

    // Validate & Post
    fun validateAndPost() {
        val trimmedText = postText.trim()
        if (trimmedText.isEmpty()) {
            showContentEmptyDialog = true
            return
        }

        if (wordCount > maxWords) {
            return
        }

        if (postId != null) {
            val existingMediaUrls = posts.firstOrNull { it.id == postId }?.media ?: emptyList()
            val newUris = selectedImageUris.filterNot { it.scheme in listOf("http", "https") }

            postViewModel.updatePost(
                postId = postId,
                request = UpdatePostRequest(trimmedText, existingMediaUrls, newUris),
                context = context
            )
        } else {
            postViewModel.createPost(
                request = CreatePostRequest(userId, userRole, trimmedText, selectedImageUris),
                context = context
            )
            navController.navigate("home")
        }
    }

    // === UI ===
    if (isLoading && postId != null) {
        LoadingOverlay()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            // Fixed Header at top
            Header(
                navController = navController,
                postText = postText,
                selectedImageUri = selectedImageUris,
                isEditMode = postId != null,
                onPost = { validateAndPost() }
            )

            // Scrollable content in the middle
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    PostBody(
                        containerPost = ContainerPost(
                            imageUrl = user?.avatarURL ?: "",
                            name = user?.name ?: "",
                            label = "Hãy nói gì đó ..."
                        ),
                        text = postText,
                        onTextChange = { postText = it },
                        wordCount = wordCount,
                        maxWords = maxWords
                    )
                }

                if (pairedList.isNotEmpty()) {
                    item {
                        MediaPreviewList(
                            context = context,
                            pairedList = pairedList,
                            onRemove = { uri ->
                                selectedImageUris = selectedImageUris - uri
                                pairedList = selectedImageUris.zip(frameUris)
                            }
                        )
                    }
                }

                if (isUpdating) {
                    item { LoadingOverlay() }
                }
            }

            // Fixed Footer at bottom
            FooterWithPermission(
                permissionState = storagePermissionState,
                onImageClick = { handleMediaSelection() }
            )
        }
    }

    if (showContentEmptyDialog) {
        EmptyContentDialog { showContentEmptyDialog = false }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ImageSearch,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Cần quyền truy cập thư viện",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Chúng tôi cần quyền truy cập vào thư viện ảnh của bạn để cho phép bạn chọn hình ảnh cho bài viết.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        storagePermissionState.launchPermissionRequest()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Cấp quyền",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Hủy",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun MediaPreviewList(
    context: Context,
    pairedList: List<Pair<Uri, Uri>>,
    onRemove: (Uri) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(pairedList) { (uri, frame) ->
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = tween(100)
            )
            
            Card(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val type = getType(uri, context)

                    if (type.contains("video")) {
                        // Video thumbnail
                        Image(
                            painter = rememberAsyncImagePainter(frame),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Video overlay gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                        
                        // Video indicator
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center)
                                .alpha(0.9f)
                        )
                    } else {
                        // Image
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Delete button with animation
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                        shadowElevation = 4.dp
                    ) {
                        IconButton(
                            onClick = { 
                                isPressed = true
                                onRemove(uri)
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Xóa media",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đang xử lý...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun EmptyContentDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Nội dung trống",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                "Vui lòng nhập nội dung hoặc chọn ít nhất một hình ảnh để đăng bài.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Đã hiểu",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun Header(
    navController: NavHostController,
    postText: String,
    selectedImageUri: List<Uri>,
    onPost: () -> Unit,
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isPostEnabled = postText.isNotBlank() || selectedImageUri.isNotEmpty()
    
    // Animated button colors
    val buttonColor by animateColorAsState(
        targetValue = if (isPostEnabled) 
            MaterialTheme.colorScheme.primary 
        else 
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300)
    )
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isPostEnabled) 1f else 0.95f,
        animationSpec = tween(200)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        )
                    )
                )
                .height(64.dp)
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = if (isEditMode) "Chỉnh sửa bài viết" else "Tạo bài viết",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.Center)
            )

            Button(
                onClick = onPost,
                enabled = isPostEnabled,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isPostEnabled) 4.dp else 0.dp
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .scale(buttonScale)
            ) {
                Text(
                    text = if (isEditMode) "Lưu" else "Đăng",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPostEnabled) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

@Composable
fun PostBody(
    containerPost: ContainerPost,
    text: String,
    onTextChange: (String) -> Unit,
    wordCount: Int,
    maxWords: Int,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with border and shadow
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                        )
                    )
                ) {
                    AsyncImage(
                        model = containerPost.imageUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    containerPost.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlineTextField(
                text = text,
                onTextChange = onTextChange,
                wordCount = wordCount,
                maxWords = maxWords
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FooterWithPermission(
    modifier: Modifier = Modifier,
    permissionState: PermissionState,
    onImageClick: () -> Unit
) {
    val isGranted = permissionState.status.isGranted
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onImageClick() }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Simple circular button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = if (isGranted)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ImageSearch,
                    contentDescription = "Thêm ảnh hoặc video",
                    tint = if (isGranted) 
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else 
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Text
            Text(
                text = when {
                    isGranted -> "Thêm ảnh hoặc video"
                    permissionState.status.shouldShowRationale -> "Cấp quyền để thêm media"
                    else -> "Yêu cầu quyền truy cập"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun countWords(text: String): Int {
    return text.length
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlineTextField(
    text: String,
    onTextChange: (String) -> Unit,
    wordCount: Int,
    maxWords: Int,
    modifier: Modifier = Modifier
){
    var isFocused by remember { mutableStateOf(false) }
    
    // Animated border color
    val borderColor by animateColorAsState(
        targetValue = when {
            wordCount > maxWords -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        animationSpec = tween(300)
    )
    
    // Animated counter color
    val counterColor by animateColorAsState(
        targetValue = when {
            wordCount > maxWords -> MaterialTheme.colorScheme.error
            wordCount > maxWords * 0.9 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300)
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                val newWordCount = countWords(newValue)
                if (newWordCount <= maxWords) {
                    onTextChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = "Hãy nói gì đó ...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .border(
                    width = 2.dp,
                    brush = if (isFocused) {
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                ),
            textStyle = MaterialTheme.typography.bodyLarge,
            minLines = 5,
            maxLines = 12,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Character counter with animation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = counterColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "$wordCount/$maxWords ký tự",
                    style = MaterialTheme.typography.labelMedium,
                    color = counterColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
