package com.hellodoc.healthcaresystem.view.user.notification
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NotificationResponse
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationPage(
    context: Context,
    navHostController: NavHostController
){
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notifications by notificationViewModel.notifications.collectAsState()

    LaunchedEffect(Unit) {
        notificationViewModel.fetchNotificationByUserId(userViewModel.getUserAttribute("userId", context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Thông báo",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
        }
        Column (
            modifier = Modifier
                .padding(horizontal = 10.dp)
        ){
        if (notifications.isEmpty()) {
            EmptyList("thông báo")
        } else {
            NotificationSectionFrame(
                navHostController = navHostController,
                notifications = notifications,
                notificationViewModel = notificationViewModel
            )
        }
    }
    }
}

@Composable
fun EmptyList(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không có $name",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationSectionFrame(
    navHostController: NavHostController,
    notifications: List<NotificationResponse>,
    notificationViewModel: NotificationViewModel
){
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



    LazyColumn (modifier = Modifier.fillMaxWidth()) {
        if (todayNotifications.isNotEmpty()) {
            item {
                Text(text = "Hôm nay", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(todayNotifications) { notification ->
                NotificationSection(
                    navHostController = navHostController,
                    notification = notification,
                    notificationViewModel = notificationViewModel
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (pastNotifications.isNotEmpty()) {
            item {
                Text(text = "Trước đó", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(pastNotifications) { notification ->
                NotificationSection(
                    navHostController = navHostController,
                    notification = notification,
                    notificationViewModel = notificationViewModel
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationSection(
    navHostController: NavHostController,
    notification: NotificationResponse,
    notificationViewModel: NotificationViewModel
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (notification.isRead) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                if (!notification.isRead) notificationViewModel.markAsRead(notification.id)
                navHostController.navigate(notification.navigatePath) },
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
        )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ){
            Image(
                painter = painterResource(R.drawable.baseline_person_24),
                contentDescription = ""
            )
            Column {
                Text(notification.content, fontWeight = FontWeight.Bold)
                Text(notification.createdAt.timeAgoInVietnam(), Modifier.alpha(0.5f))
                Text("Nhấn vào đây để xem chi tiết")
            }
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
