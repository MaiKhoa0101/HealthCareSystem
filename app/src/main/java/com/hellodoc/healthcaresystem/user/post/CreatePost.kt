
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

//var userId=""
//var userModel= ""

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CreatePostScreen(
    context: Context,
    navController: NavHostController,
    postId: String? = null,
    modifier: Modifier = Modifier
) {
    val storagePermissionState: PermissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    var showPermissionDialog by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var userModel by remember { mutableStateOf("") }

    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })

    val user by userViewModel.user.collectAsState()
    var avatarUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userModel = userViewModel.getUserAttributeString("role")
        userViewModel.getUser(userId)
        avatarUrl = user?.avatarURL ?: ""
        username = user?.name ?: ""

        // Nếu có postId thì gọi API lấy thông tin bài viết
        postId?.let { id ->
            postViewModel.getPostById(id)
        }
    }

    var selectedImageUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var postText by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        selectedImageUri += uris
    }

    val posts by postViewModel.posts.collectAsState()
    val updateSuccess by postViewModel.updateSuccess.collectAsState()
    val isUpdating by postViewModel.isUpdating.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()

    // Theo dõi khi có bài viết được load
    LaunchedEffect(posts) {
        if (postId != null && posts.isNotEmpty()) {
            val post = posts.firstOrNull { it.id == postId }
            post?.let {
                postText = it.content ?: ""
                selectedImageUri = it.media?.mapNotNull { uri -> Uri.parse(uri) } ?: emptyList()
            }
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            postViewModel.resetUpdateSuccess()
            navController.currentBackStackEntry?.savedStateHandle?.set("shouldReload", true)
            navController.navigate("home")
        }
    }

    fun handleImageSelection() {
        when {
            storagePermissionState.status.isGranted -> {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            }
            storagePermissionState.status.shouldShowRationale -> {
                showPermissionDialog = true
            }
            else -> {
                storagePermissionState.launchPermissionRequest()
            }
        }
    }

    if (isLoading && postId != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
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
                    selectedImageUri = selectedImageUri,
                    isEditMode = postId != null,
                    onPost = {
                        if (postId != null) {
//                            postViewModel.updatePost(
//                                postId = postId,
//                                request = UpdatePostRequest(
//                                    content = postText,
//                                    media = media,
//                                    images = selectedImageUri
//                                ),
//                                context = context
//                            )
                        } else {
                            postViewModel.createPost(
                                request = CreatePostRequest(userId, userModel, postText, selectedImageUri),
                                context = context
                            )
                            navController.navigate("personal")
                        }
                    }
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
                    onTextChange = { postText = it }
                )
            }

            item {
                if (selectedImageUri.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(selectedImageUri) { uri ->
                            Box(
                                modifier = Modifier.size(200.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = {
                                        selectedImageUri = selectedImageUri.toMutableList().apply {
                                            remove(uri)
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(21.dp)
                                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), shape = CircleShape),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xóa ảnh",
                                        tint = MaterialTheme.colorScheme.background,
                                        modifier = Modifier.size(30.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                FooterWithPermission(
                    permissionState = storagePermissionState,
                    onImageClick = { handleImageSelection() }
                )
            }

            item {
                if (isUpdating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.background)
                    }
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Cần quyền truy cập thư viện") },
            text = {
                Text("Chúng tôi cần quyền truy cập vào thư viện ảnh của bạn để cho phép bạn chọn hình ảnh cho bài viết.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        storagePermissionState.launchPermissionRequest()
                    }
                ) {
                    Text("Cấp quyền")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
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
                .clickable { navController.navigate("home") }
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
    modifier: Modifier = Modifier
){
    val backgroundColor = MaterialTheme.colorScheme.background
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(500.dp)
            .fillMaxWidth()
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
            modifier = Modifier.constrainAs(textField) {
                top.linkTo(iconImage.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FooterWithPermission(
    modifier: Modifier = Modifier,
    permissionState: PermissionState,
    onImageClick: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background

    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .fillMaxSize()
            .height(250.dp)
    ) {
        val horizontalGuideLine30 = createGuidelineFromTop(0.3f)
        val (iconImage, tvTitle, topLine, permissionText) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .constrainAs(topLine) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        when {
            permissionState.status.isGranted -> {
                // Hiển thị button bình thường khi đã có quyền
                Image(
                    painter = painterResource(id = R.drawable.ic_attach_file),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clickable { onImageClick() }
                        .constrainAs(iconImage) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(horizontalGuideLine30)
                        },
                )
                Text(
                    text = "Thêm hình ảnh",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.constrainAs(tvTitle) {
                        top.linkTo(iconImage.bottom, margin = 5.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }

            else -> {
                // Hiển thị button yêu cầu quyền
                Image(
                    painter = painterResource(id = R.drawable.ic_attach_file),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clickable { onImageClick() }
                        .constrainAs(iconImage) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(horizontalGuideLine30)
                        },
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
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.constrainAs(tvTitle) {
                        top.linkTo(iconImage.bottom, margin = 5.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlineTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            onTextChange(newValue)
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
}

// Các function không được sử dụng - có thể xóa hoặc giữ lại để tham khảo
@Composable
fun Footer(
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit
){
    val backgroundColor = Color.White
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .fillMaxSize()
            .height(250.dp)
    ) {
        val horizontalGuideLine30 = createGuidelineFromTop(0.3f)
        val (iconImage, tvTitle, topLine) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color.LightGray)
                .constrainAs(topLine) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_attach_file),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clickable { onImageClick() }
                .constrainAs(iconImage){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(horizontalGuideLine30)
                },
        )
        Text(
            text = "Thêm hình ảnh",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.Gray
            ),
            modifier = Modifier.constrainAs(tvTitle){
                top.linkTo(iconImage.bottom, margin = 5.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Composable
fun MultiFileUpload(){
    var selectedImageUri: List<Uri> by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    var MultiplePhotoPickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)) {
            uri ->
        selectedImageUri = uri
    }
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        LazyRow {
            items(selectedImageUri){uri ->
                Image(painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            } }
        Button(
            onClick ={ MultiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
            )}
        ) {
            Text("Chọn Ảnh")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FileUpload(){
    var selectImageUri by remember { mutableStateOf<Uri?>(null) }
    val storagePermissionState : PermissionState = rememberPermissionState(permission = android.Manifest.permission.READ_MEDIA_IMAGES)
    val ImagePickerLauncher : ManagedActivityResultLauncher<String, Uri?> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
            uri: Uri? ->  selectImageUri = uri
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        selectImageUri?.let {
                uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier.size(200.dp).padding(16.dp),
                contentScale = ContentScale.Crop
            )
        }
        when {
            storagePermissionState.status.isGranted->{
                Button(onClick = {ImagePickerLauncher.launch("image/*")}){
                    Text("Chọn ảnh trong thư viện")
                }
            }
            storagePermissionState.status.shouldShowRationale->{
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Chúng tôi cần quyền truy cập vào thư viện của bạn để cho phép bạn chọn hình ảnh.")
                }
                Button(onClick = {storagePermissionState.launchPermissionRequest()}){
                    Text("Cấp quyền")
                }
            }
            else->{
                Button(onClick = {storagePermissionState.launchPermissionRequest()}){
                    Text("Yêu cầu quyền truy cập thư viện")
                }
            }
        }
    }
}