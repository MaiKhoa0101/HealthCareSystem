package com.hellodoc.healthcaresystem.view.user.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
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

    // Pull to refresh handler
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

    // Update notifications when filter changes
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
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        // Enhanced Header with unread badge and actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                // Title with badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Thông báo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        UnreadBadge(count = unreadCount)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter and Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !showOnlyUnread,
                            onClick = { showOnlyUnread = false },
                            label = { Text("Tất cả") },
                            leadingIcon = if (!showOnlyUnread) {
                                { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = showOnlyUnread,
                            onClick = { showOnlyUnread = true },
                            label = { Text("Chưa đọc") },
                            leadingIcon = if (showOnlyUnread) {
                                { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }

                    // Mark all as read button
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                notificationViewModel.markAllAsRead(userId)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark all as read",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đọc hết")
                        }
                    }
                }
            }
        }

        // Content with pull-to-refresh
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

            // Pull to refresh indicator
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
        modifier = Modifier.size(28.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
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
        // Animated icon
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
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .alpha(0.6f),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
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
    val sortedNotifications = notifications.sortedByDescending { notification ->
        Instant.parse(notification.createdAt)
    }

    val today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))

    val (todayNotifications, pastNotifications) = sortedNotifications.partition { notification ->
        try {
            val instant = Instant.parse(notification.createdAt)
            val vietnamDate = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate()
            vietnamDate == today
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (todayNotifications.isNotEmpty()) {
            item {
                SectionHeader(title = "Hôm nay")
            }
            items(
                items = todayNotifications,
                key = { it.id }
            ) { notification ->
                AnimatedNotificationCard(
                    navHostController = navHostController,
                    notification = notification,
                    notificationViewModel = notificationViewModel
                )
            }
        }

        if (pastNotifications.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Trước đó")
            }
            items(
                items = pastNotifications,
                key = { it.id }
            ) { notification ->
                AnimatedNotificationCard(
                    navHostController = navHostController,
                    notification = notification,
                    notificationViewModel = notificationViewModel
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
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

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) +
                expandVertically(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200)) +
                shrinkVertically(animationSpec = tween(200))
    ) {
        NotificationCard(
            navHostController = navHostController,
            notification = notification,
            notificationViewModel = notificationViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationCard(
    navHostController: NavHostController,
    notification: NotificationResponse,
    notificationViewModel: NotificationViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                // Show confirmation dialog
                showDeleteDialog = true
            }
            // Return false to allow swiping back
            false
        }
    )

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa thông báo") },
            text = { Text("Bạn có chắc chắn muốn xóa thông báo này?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        notificationViewModel.deleteNotification(notification.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.padding(end = 24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        enableDismissFromStartToEnd = false,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!notification.isRead) {
                        notificationViewModel.markAsRead(notification.id)
                    }
                    navHostController.navigate(notification.navigatePath)
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (notification.isRead)
                    MaterialTheme.colorScheme.surface
                else
                    MaterialTheme.colorScheme.tertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (notification.isRead) 1.dp else 3.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Notification Icon
                NotificationIcon(
                    isRead = notification.isRead,
                    content = notification.content
                )

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = notification.content,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = notification.createdAt.timeAgoInVietnam(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    if (!notification.isRead) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = "Nhấn để xem chi tiết",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Chevron
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
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
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
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
            duration.toMinutes() < 1 -> "Vừa xong"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
            duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
            duration.toDays() == 1L -> "Hôm qua"
            duration.toDays() < 7 -> "${duration.toDays()} ngày trước"
            else -> vietnamTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    } catch (e: Exception) {
        "Không xác định"
    }
}
