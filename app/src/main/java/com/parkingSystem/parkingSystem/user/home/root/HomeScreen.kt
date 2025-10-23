package com.parkingSystem.parkingSystem.user.home.root
import com.parkingSystem.parkingSystem.R
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.parkingSystem.core.common.skeletonloading.SkeletonBox
import com.parkingSystem.parkingSystem.responsemodel.*
import com.parkingSystem.parkingSystem.viewmodel.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthMateHomeScreen(
    modifier: Modifier = Modifier,
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    userViewModel: UserViewModel,
    parkingViewModel: ParkingViewModel
) {
    val context = LocalContext.current
    val listState = rememberSaveable(
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }
    val coroutineScope = rememberCoroutineScope()

    val isScrollButtonVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 3 //hien thi khi scroll đến vị trí thứ 3
        }
    }

    // Collect states with loading information
    var showReportBox by remember { mutableStateOf(false) }
    var userModel by remember { mutableStateOf("") }
    var username = ""

    val parks by parkingViewModel.parks.collectAsState()
    LaunchedEffect(Unit) {
        username = userViewModel.getUserAttributeString("name")
        userModel = userViewModel.getUserAttributeString("role")

        println("USERNNAME trong HomeScreen: $username")

        println("USERID tronng HomeScreen: ${userViewModel.getUserAttributeString("userId")}")
        userViewModel.getAllUserAttributeString()
        userViewModel.getUser(userViewModel.getUserAttributeString("id"))
        parkingViewModel.fetchAllParksAvailable()
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    showReportBox = false
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .background(MaterialTheme.colorScheme.background),
            state = listState
        ) {
            item(key = "specialties") {
                if (parks.isEmpty()) {
                    SpecialtySkeletonList()
                } else {
                    ParkList(
                        navHostController = navHostController,
                        context = context,
                        parks = parks
                    )
                }
            }
        }

        if (isScrollButtonVisible) {
            BackToTopButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },

            )
        }
    }
}

@Composable
fun BackToTopButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animation shimmer
    val transition = rememberInfiniteTransition(label = "shimmer")
    // Animation chạy dọc trục Y (từ dưới lên trên)
    val translateAnim = transition.animateFloat(
        initialValue = 400f,   // bắt đầu ở dưới
        targetValue = -400f,   // chạy ngược lên trên
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            Color.Transparent
        ),
        start = Offset(x = 0f, y = translateAnim.value + 200f), // dưới
        end   = Offset(x = 0f, y = translateAnim.value)         // trên
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .background(shimmerBrush)   // shimmer overlay
            .clickable { onClick() },   // thay vì FloatingActionButton
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardDoubleArrowUp,
            contentDescription = "Back to Top",
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


@Composable
fun SpecialtySkeletonList() {
    Column {
        // Header skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp),
                shape = RoundedCornerShape(4.dp)
            )

            SkeletonBox(
                modifier = Modifier
                    .width(60.dp)
                    .height(16.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            items(6) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SkeletonBox(
                            modifier = Modifier
                                .size(80.dp)
                                .height(80.dp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ParkList(
    navHostController: NavHostController,
    context: Context,
    parks: List<Park>
) {

    Column {
        // Header row with title and "See more" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Parking lot",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(max = 800.dp), // Bắt buộc có giới hạn
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            items (parks)
            { park ->
                if (parks.isEmpty()) {
                    Text("Không có dịch vụ nào")
                }
                else {
                    ParkItem(
                        navHostController = navHostController,
                        park = park,
                        onClick = { showToast(context, "Đã chọn: ${park.parkName}") }
                    )
                }
            }
        }

    }
}

@Composable
fun ParkItem(
    navHostController: NavHostController,
    park: Park,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp))
            .clickable {
                firebaseAnalytics.logEvent("park_selected", bundleOf(
                    "ID_park" to park.parkId,
                    "Name_of_park" to park.parkName,
                ))
                onClick()
                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("parkId", park.parkId)
                }
                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("parkName", park.parkName)
                }
                navHostController.navigate("slot_list/${park.parkId}")
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = park.parkName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(), // Đảm bảo chiếm hết chiều rộng để căn giữa chính xác
                maxLines = 2,
                overflow = TextOverflow.Ellipsis, // Thêm dấu "..." nếu quá 2 dòng
                softWrap = true // Cho phép xuống dòng mềm
            )
        }
    }
}

