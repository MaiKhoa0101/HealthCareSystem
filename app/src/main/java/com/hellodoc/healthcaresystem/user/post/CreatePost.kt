
package com.hellodoc.healthcaresystem.user.post

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.user.supportfunction.getType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.user.supportfunction.extractOneFrame
import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
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
    postViewModel: PostViewModel,
    postId: String? = null
) {
    val storagePermissionState: PermissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        }
        else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    var showPermissionDialog by remember { mutableStateOf(false) }

    // User info
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

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
        userId = userViewModel.getUserAttributeString("userId")
        userRole = userViewModel.getUserAttributeString("role")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            item {
                Header(
                    navController = navController,
                    postText = postText,
                    selectedImageUri = selectedImageUris,
                    isEditMode = postId != null,
                    onPost = { validateAndPost() }
                )
            }

            item {
                PostBody(
                    containerPost = ContainerPost(
                        imageUrl = user?.avatarURL ?: "",
                        name = user?.name ?: "",
                        label = "Hãy nói gì đó ..."
                    ),
                    text = postText,
                    onTextChange = { postText = it  },
                    wordCount = wordCount,
                    maxWords = maxWords
                )
            }

            item {
                Column {
                    FooterWithPermission(
                        permissionState = storagePermissionState,
                        onImageClick = { handleMediaSelection() }
                    )
                    if (pairedList.isNotEmpty()) {
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
            }

            if (isUpdating) {
                item { LoadingOverlay() }
            }
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
            title = { Text("Cần quyền truy cập thư viện") },
            text = { Text("Chúng tôi cần quyền truy cập vào thư viện ảnh của bạn để cho phép bạn chọn hình ảnh cho bài viết.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        storagePermissionState.launchPermissionRequest()
                    }
                ) {
                    Text("Cấp quyền")
                } },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Hủy")
                }
            }
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(pairedList) { (uri, frame) ->
            Box(
                modifier = Modifier.size(200.dp)
            ) {
                val type = getType(uri, context)

                if (type.contains("video")) {
                    // Video -> hiển thị thumbnail
                    Image(
                        painter = rememberAsyncImagePainter(frame),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Image
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Nút xóa
                IconButton(
                    onClick = { onRemove(uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            CircleShape
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Xóa media",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(20.dp),
                    )
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
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
@Composable
fun EmptyContentDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Nội dung trống",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            )
        },
        text = {
            Text("Vui lòng nhập nội dung hoặc chọn ít nhất một hình ảnh để đăng bài.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đã hiểu", style = TextStyle(fontWeight = FontWeight.SemiBold))
            }
        }
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
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val isPostEnabled = postText.isNotBlank() || selectedImageUri.isNotEmpty()

    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(60.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        val horizontalGuideLine10 = createGuidelineFromTop(0.1f)
        val (iconImage, tvTitle, tvButt) = createRefs()
        Image(
            painterResource(id = R.drawable.arrow_back),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clickable { navController.popBackStack() }
                .constrainAs(iconImage){
                    start.linkTo(parent.start, margin = 10.dp)
                    top.linkTo(horizontalGuideLine10, margin = 10.dp)
                },
        )
        Text(
            text = if (isEditMode) "Chỉnh sửa bài viết" else "Tạo bài viết",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.constrainAs(tvTitle){
                top.linkTo(horizontalGuideLine10, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Button(
            onClick = onPost,
            enabled = isPostEnabled,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(80.dp).height(40.dp)
                .constrainAs(tvButt){
                    top.linkTo(horizontalGuideLine10)
                    end.linkTo(parent.end, margin = 3.dp)
                }) {
            Text(
                text = if (isEditMode) "Lưu" else "Đăng",
                modifier= Modifier.padding(0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
            )
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
    val backgroundColor = MaterialTheme.colorScheme.background
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        val horizontalGuideLine50 = createGuidelineFromTop(0.05f)
        val (iconImage, tvName, textField) = createRefs()
        AsyncImage(
            model = containerPost.imageUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .clip(CircleShape)
                .size(45.dp)
                .constrainAs(iconImage){
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(horizontalGuideLine50)
                },
            contentScale = ContentScale.Crop
        )
        Text(
            containerPost.name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.constrainAs(tvName){
                top.linkTo(horizontalGuideLine50, margin = 5.dp)
                start.linkTo(iconImage.end, margin = 10.dp)
            }
        )
        OutlineTextField(
            text = text,
            onTextChange = onTextChange,
            wordCount = wordCount,
            maxWords = maxWords,
            modifier = Modifier.constrainAs(textField) {
                top.linkTo(iconImage.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FooterWithPermission(
    modifier: Modifier = Modifier,
    permissionState: PermissionState,
    onImageClick: () -> Unit
) {
    HorizontalDivider()
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
    ) {
        when {
            permissionState.status.isGranted -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        // Hiển thị button bình thường khi đã có quyền
                        Image(
                            imageVector = Icons.Default.ImageSearch,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .size(30.dp)
                                .offset(x = (10).dp, y = (10).dp) // dịch sang trái và lên
                        )
                        // Hiển thị button bình thường khi đã có quyền
                        Image(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .size(30.dp)
                                .offset(x = (-10).dp, y = (-10).dp) // dịch sang phải và xuống
                        )
                        Text(
                            text = "Thêm tệp",
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.offset(x = (0).dp, y = (40).dp)
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .width(180.dp)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        // Hiển thị button bình thường khi đã có quyền
                        Image(
                            imageVector = Icons.Default.ImageSearch,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .offset(x = (10).dp, y = (10).dp) // dịch sang trái và lên
                        )
                        // Hiển thị button bình thường khi đã có quyền
                        Image(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .offset(x = (-10).dp, y = (-10).dp) // dịch sang phải và xuống
                        )
                        Text(
                            text = if (permissionState.status.shouldShowRationale) {
                                "Cấp quyền để thêm ảnh"
                            } else {
                                "Yêu cầu quyền truy cập"
                            },
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            ),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .offset(x = (0).dp, y = (40).dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
    HorizontalDivider()
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
    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            // Chỉ cho phép nhập nếu không vượt quá giới hạn từ
            val newWordCount = countWords(newValue)
            if (newWordCount <= maxWords) {
                onTextChange(newValue)

            }
        },
        placeholder = {
            Text(
                text = "Hãy nói gì đó ...",
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        maxLines = 10,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondaryContainer,
            errorPlaceholderColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledPlaceholderColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedPlaceholderColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent
        )
    )

    // Hiển thị word count
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "$wordCount/$maxWords chữ",
            style = TextStyle(
                fontSize = 12.sp,
                color = if (wordCount > maxWords) {
                    MaterialTheme.colorScheme.error
                } else if (wordCount > maxWords * 0.9) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        )
    }
}
