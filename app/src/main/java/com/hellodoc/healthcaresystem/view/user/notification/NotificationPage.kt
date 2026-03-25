package com.hellodoc.healthcaresystem.view.user.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NotificationResponse
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationPage(
    context: Context,
    navHostController: NavHostController
) {
    val userViewModel: UserViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notifications by notificationViewModel.notifications.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var showOnlyUnread by remember { mutableStateOf(false) }
    val userId = userViewModel.getUserAttribute("userId", context)

    LaunchedEffect(Unit) {
        notificationViewModel.fetchNotificationByUserId(userId)
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            if (showOnlyUnread) {
                notificationViewModel.fetchUnreadNotifications(userId)
            } else {
                notificationViewModel.fetchNotificationByUserId(userId)
            }
            delay(1000)
            isRefreshing = false
        }
    }

    LaunchedEffect(showOnlyUnread) {
        if (showOnlyUnread) {
            notificationViewModel.fetchUnreadNotifications(userId)
        } else {
            notificationViewModel.fetchNotificationByUserId(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Thông báo",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(10.dp))
                        UnreadBadge(count = unreadCount)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !showOnlyUnread,
                            onClick = { showOnlyUnread = false },
                            label = { Text("Tất cả") },
                            leadingIcon = if (!showOnlyUnread) {
                                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = showOnlyUnread,
                            onClick = { showOnlyUnread = true },
                            label = { Text("Chưa đọc") },
                            leadingIcon = if (showOnlyUnread) {
                                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                            } else null
                        )
                    }

                    if (unreadCount > 0) {
                        TextButton(
                            onClick = { notificationViewModel.markAllAsRead(userId) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đọc hết", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (notifications.isEmpty()) {
                EnhancedEmptyState()
            } else {
                NotificationSectionFrame(
                    navHostController = navHostController,
                    notifications = notifications,
                    notificationViewModel = notificationViewModel
                )
            }

            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun UnreadBadge(count: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(26.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EnhancedEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "bell")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(100.dp).scale(scale).alpha(0.5f),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Chưa có thông báo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Các thông báo mới sẽ xuất hiện ở đây",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationSectionFrame(
    navHostController: NavHostController,
    notifications: List<NotificationResponse>,
    notificationViewModel: NotificationViewModel
) {
    val sortedNotifications = notifications.sortedByDescending { Instant.parse(it.createdAt) }
    val today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))

    val (todayNotifications, pastNotifications) = sortedNotifications.partition {
        try {
            Instant.parse(it.createdAt)
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDate() == today
        } catch (e: Exception) { false }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (todayNotifications.isNotEmpty()) {
            item { SectionHeader(title = "Hôm nay") }
            items(todayNotifications, key = { it.id }) { notification ->
                AnimatedNotificationCard(navHostController, notification, notificationViewModel)
            }
        }
        if (pastNotifications.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "Trước đó")
            }
            items(pastNotifications, key = { it.id }) { notification ->
                AnimatedNotificationCard(navHostController, notification, notificationViewModel)
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnimatedNotificationCard(
    navHostController: NavHostController,
    notification: NotificationResponse,
    notificationViewModel: NotificationViewModel
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
        exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
    ) {
        NotificationCard(navHostController, notification, notificationViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationCard(
    navHostController: NavHostController,
    notification: NotificationResponse,
    notificationViewModel: NotificationViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa thông báo") },
            text = { Text("Bạn có chắc chắn muốn xóa thông báo này?") },
            confirmButton = {
                TextButton(onClick = {
                    notificationViewModel.deleteNotification(notification.id)
                    showDeleteDialog = false
                }) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showBottomSheet = false
                        showDeleteDialog = true
                    }
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    "Xóa thông báo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ── Card chính ──────────────────────────────────────────────────────────
    val isUnread = !notification.isRead
    val cardBg = if (isUnread)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = {
                    if (isUnread) notificationViewModel.markAsRead(notification.id)
                    navHostController.navigate(notification.navigatePath){
                        popUpTo(navHostController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onLongClick = { showBottomSheet = true }
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (isUnread)
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Icon ──────────────────────────────────────────────────────
            NotificationIcon(isRead = !isUnread, content = notification.content)

            // ── Nội dung ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = notification.content,
                    fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                    Text(
                        text = notification.createdAt.timeAgoInVietnam(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )

                    // Dot "chưa đọc" nhỏ gọn nằm cùng hàng thời gian
                    if (isUnread) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "Mới",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // ── Chevron ───────────────────────────────────────────────────
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NotificationIcon(isRead: Boolean, content: String) {
    val icon = when {
        content.contains("lịch hẹn", ignoreCase = true) ||
                content.contains("appointment", ignoreCase = true) -> Icons.Outlined.CalendarToday
        content.contains("thuốc", ignoreCase = true) ||
                content.contains("medication", ignoreCase = true) -> Icons.Outlined.MedicalServices
        content.contains("thanh toán", ignoreCase = true) ||
                content.contains("payment", ignoreCase = true) -> Icons.Outlined.Payment
        content.contains("bác sĩ", ignoreCase = true) ||
                content.contains("doctor", ignoreCase = true) -> Icons.Outlined.Person
        content.contains("kết quả", ignoreCase = true) ||
                content.contains("result", ignoreCase = true) -> Icons.Outlined.Assignment
        else -> Icons.Outlined.Notifications
    }

    Surface(
        shape = CircleShape,
        color = if (isRead)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = if (isRead)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.timeAgoInVietnam(): String {
    return try {
        val instant = Instant.parse(this)
        val vietnamTime = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
        val now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        val duration = Duration.between(vietnamTime, now)
        when {
            duration.toMinutes() < 1  -> "Vừa xong"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
            duration.toHours()   < 24 -> "${duration.toHours()} giờ trước"
            duration.toDays()  == 1L  -> "Hôm qua"
            duration.toDays()   < 7  -> "${duration.toDays()} ngày trước"
            else -> vietnamTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    } catch (e: Exception) { "Không xác định" }
}