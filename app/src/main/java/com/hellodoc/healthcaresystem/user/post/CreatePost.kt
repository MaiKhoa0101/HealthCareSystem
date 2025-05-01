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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.rememberAsyncImagePainter

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

var userId: String = ""
var userModel: String = ""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostScreen(context: Context, navController: NavHostController, modifier: Modifier = Modifier
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userModel = if (userViewModel.getUserAttributeString("role") == "user") "User" else "Doctor"

    }

    var selectedImageUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var postText by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        selectedImageUri += uris
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Đổi màu background ở đây
    ) {
        item {
            Header(
                navController = navController,
                postText = postText,
                selectedImageUri = selectedImageUri,
                onPost = {
                    postViewModel.createPost(CreatePostRequest(userId, userModel, postText, selectedImageUri), context)
                    navController.navigate("personal")
                }
            )
        }
        item {
            PostBody(
                containerPost = ContainerPost(
                    imageUrl = R.drawable.img.toString(),
                    name = "Khoa xinh gái",
                    lable = "Hãy nói gì đó ..."
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
                            modifier = Modifier
                                .size(200.dp)
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
                                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape),

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Xóa ảnh",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp),
                                )
                            }
                        }
                    }
                }
            }

            Footer(
                onImageClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                }
            )
        }
    }
}

@Composable
fun Header(
    navController: NavHostController,
    postText: String,
    selectedImageUri: List<Uri>,
    onPost: () -> Unit,
    modifier: Modifier = Modifier
){
    val backgroundColor = Color.Cyan
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
            text = "Tạo bài viết",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.Black
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
                text = "Đăng",
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
    val backgroundColor = Color.White
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(500.dp)
            .fillMaxWidth()
    ) {
        val horizontalGuideLine50 = createGuidelineFromTop(0.05f)
        val (iconImage, tvName, textField) = createRefs()
        Image(
            painter = rememberAsyncImagePainter(containerPost.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(45.dp)
                .constrainAs(iconImage){
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(horizontalGuideLine50)
//                height = Dimension.fillToConstraints //keo anh dai het height
                },
            contentScale = ContentScale.Crop
        )
        Text(
            containerPost.name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Black
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
//                height = Dimension.fillToConstraints //keo anh dai het height
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
                color = Color.Gray
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        maxLines = 10,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedPlaceholderColor = Color.Gray,
            errorPlaceholderColor = Color.Gray,
            disabledPlaceholderColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent
        )
    )
}

@Composable
fun MultiFileUpload(){
    var selectedImageUri: List<Uri> by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    var MultiplePhotoPickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)) {
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


