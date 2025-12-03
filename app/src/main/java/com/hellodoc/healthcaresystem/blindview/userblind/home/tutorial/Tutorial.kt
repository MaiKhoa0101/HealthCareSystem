package com.hellodoc.healthcaresystem.blindview.userblind.home.tutorial

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.speakQueue
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun Tutorial(
    navHostController: NavHostController,
    you: User?
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        SoundManager.init(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            FocusTTS.shutdown()
            SoundManager.release()
        }
    }

    if (you == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Đang tải thông tin...")
        }
        return
    }

    var state by remember { mutableIntStateOf(0) }

    when (state) {
        0 -> Tutorial1(you) { state++ }
        1 -> Tutorial2(you, { state++ }, { state-- })
        2 -> Tutorial3(you, { state++ }, { state-- })
        3 -> Tutorial4(you, { state++ }, { state-- })
        4 -> Tutorial5(you, { state++ }, { state-- })
        5 -> Tutorial6(you) {
            navHostController.navigate("home") {
                popUpTo("tutorial") { inclusive = true }
            }
        }
    }
}

@Composable
fun Tutorial1(you: User, next: () -> Unit) {
    val context = LocalContext.current
    val name = you.name ?: "bạn"
    var speaking by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        speaking = true
        delay(3000)
        speakQueue(
            "Xin chào $name!",
            "Tôi sẽ hướng dẫn bạn sử dụng ứng dụng này thật dễ hiểu.",
            "Các thao tác bạn cần biết là chạm, trượt và giữ.",
            "Bây giờ, hãy chạm vào màn hình để tiếp tục."
        )
        speaking = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .clickable(enabled = !speaking) {
                SoundManager.playTap()
                vibrate(context)
                next()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(if (speaking) "Đang hướng dẫn..." else "Chạm để tiếp tục")
    }
}

@Composable
fun Tutorial2(you: User, next: () -> Unit, back: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var canTap by remember { mutableStateOf(false) }
    var readDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        speakQueue(
            "Tốt lắm ${you.name}.",
            "Trên màn hình có một bài viết.",
            "Nếu muốn nghe tôi đọc bài viết, hãy chạm vào nó.",
            "Hãy chạm để tôi đọc cho bạn nhé."
        )
        canTap = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .clickable {
                if (!canTap) return@clickable

                if (!readDone) {
                    readDone = true
                    SoundManager.playTap()
                    vibrate(context)
                    scope.launch {
                        speakQueue(
                            "Bạn làm tốt lắm, tôi sẽ đọc bài viết.",
                            "Bài viết: Chào mừng đến với hello doc, đây là bài viết mẫu để giới thiệu về ứng dụng hello doc.",
                            "Đây là ảnh minh họa, ảnh có nội dung là 1 bác sĩ đang nói chuyện trước màn hình.",
                            "Chạm thêm một lần nữa để tiếp tục."
                        )
                    }
                } else {
                    SoundManager.playTap()
                    vibrate(context)
                    next()
                }
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        ExamplePost()
    }
}

@Composable
fun Tutorial3(you: User, next: () -> Unit, back: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var offsetY by remember { mutableFloatStateOf(0f) }
    var swipedUp by remember { mutableStateOf(false) } // Đổi tên biến để phản ánh Trượt Lên trước
    var swipedDown by remember { mutableStateOf(false) }
    var instructedSwipeDown by remember { mutableStateOf(false) } // Đổi tên hướng dẫn
    var canLongPress by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        speakQueue(
            "Tiếp theo là thao tác trượt.",
            "Bạn hãy trượt lên, thao tác trượt lên sẽ lấy bài viết mới cho bạn."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        // Reset offset sau mỗi lần thả tay
                        offsetY = 0f
                    }
                ) { _, dragAmount ->
                    offsetY += dragAmount

                    // Phát hiện trượt LÊN (offsetY < -100) -> Lấy bài mới
                    if (!swipedUp && offsetY < -100) {
                        swipedUp = true
                        SoundManager.playSwipe()
                        vibrate(context)
                        scope.launch {
                            FocusTTS.speakAndWait("Bạn đã trượt lên chính xác, bài viết mới đã được lấy.")
                            delay(500)
                            FocusTTS.speakAndWait("Giờ bạn hãy trượt xuống để quay lại bài viết trước đó.")
                            instructedSwipeDown = true
                        }
                    }

                    // Phát hiện trượt XUỐNG (chỉ sau khi đã trượt lên) (offsetY > 100) -> Quay lại bài cũ
                    if (swipedUp && instructedSwipeDown && !swipedDown && offsetY > 100) {
                        swipedDown = true
                        SoundManager.playSwipe()
                        vibrate(context)
                        scope.launch {
                            FocusTTS.speakAndWait("Bạn đã trượt xuống chính xác, thao tác này giúp bạn quay lại bài viết trước đó.")
                            delay(500)
                            FocusTTS.speakAndWait("Để tiếp tục, bạn hãy nhấn giữ vào màn hình.")
                            canLongPress = true
                        }
                    }
                }
            }
            .pointerInput(canLongPress) {
                if (canLongPress) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                SoundManager.playHold()
                                vibrate(context, 60)
                                FocusTTS.speakAndWait("Bạn đã nhấn giữ thành công.")
                                delay(500)
                                next()
                            }
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Hiển thị bài viết khác nhau dựa trên trạng thái
        if (swipedUp && !swipedDown) {
            ExamplePost1()
        } else {
            ExamplePost()
        }
    }
}

@Composable
fun Tutorial4(you: User, next: () -> Unit, back: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var longPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        speakQueue(
            "Tiếp theo là thực hành lại thao tác nhấn giữ.",
            "Nhấn giữ thường dùng để mở menu hoặc các tùy chọn nâng cao.",
            "Hãy nhấn giữ vào màn hình."
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (!longPressed) {
                            longPressed = true
                            SoundManager.playHold()
                            vibrate(context, 60)
                            scope.launch {
                                FocusTTS.speakAndWait("Bạn đã nhấn giữ thành công.")
                                delay(500)
                                FocusTTS.speakAndWait("Chạm vào màn hình để tiếp tục.")
                            }
                        }
                    },
                    onTap = {
                        if (longPressed) {
                            SoundManager.playTap()
                            vibrate(context)
                            next()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(if (!longPressed) "Nhấn giữ vào màn hình" else "Chạm để tiếp tục")
    }
}

@Composable
fun Tutorial5(you: User, next: () -> Unit, back: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var done by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        speakQueue(
            "Hãy thử lại các thao tác vừa học.",
            "Bạn có thể chạm, trượt hoặc nhấn giữ để tiếp tục."
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!done) {
                            done = true
                            SoundManager.playTap()
                            vibrate(context)
                            scope.launch {
                                FocusTTS.speakAndWait("Bạn đã chạm.")
                                delay(500)
                                next()
                            }
                        }
                    },
                    onLongPress = {
                        if (!done) {
                            done = true
                            SoundManager.playHold()
                            vibrate(context, 60)
                            scope.launch {
                                FocusTTS.speakAndWait("Bạn đã nhấn giữ.")
                                delay(500)
                                next()
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { offsetX = 0f }
                ) { _, drag ->
                    offsetX += drag
                    if (!done && kotlin.math.abs(offsetX) > 100) {
                        done = true
                        SoundManager.playSwipe()
                        vibrate(context)
                        scope.launch {
                            FocusTTS.speakAndWait("Bạn đã trượt đúng.")
                            delay(500)
                            next()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Thực hiện một thao tác bất kỳ để tiếp tục")
    }
}

@Composable
fun Tutorial6(you: User, finish: () -> Unit) {
    val context = LocalContext.current
    val name = you.name ?: "bạn"

    LaunchedEffect(Unit) {
        speakQueue(
            "Chúc mừng $name.",
            "Bạn đã học xong tất cả thao tác cơ bản.",
            "Chạm vào màn hình để bắt đầu sử dụng ứng dụng."
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .clickable {
                SoundManager.playTap()
                vibrate(context)
                finish()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Chạm để vào ứng dụng")
    }
}
