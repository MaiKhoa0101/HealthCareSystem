package com.hellodoc.healthcaresystem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    //Màu chủ đạo app chế độ tối (xám đen)
    primaryContainer = BoxGrey,
    //Đối tượng trên nền màu chủ đạo
    onPrimaryContainer = Color.White,
    //Màu nền phần nội dung (xám đen tối)
    background = Color(0xFF2A2A2A),
    //Đối tượng trên background (trắng)
    onBackground = Color(0xFFFFFFFF),
    //Màu nền nội dung cấp 2
    secondary = LightDarkTheme,
//    onSecondary = Color.DarkGray,
    //Màu nền nội dung cấp 2 (xám tối) thường dùng cho các card nằm trên background
    secondaryContainer = BoxLightGrey,
    // Nội dung trên card
    onSecondaryContainer = onSecondDarkContainer,
    //Màu nền nội dung cấp 3 (xanh mờ) thường dùng cho các component như thông báo chưa xem
    tertiaryContainer = LightDarkTheme,


    )

private val LightColorScheme = lightColorScheme(
    //Màu chủ đạo app xanh
    primaryContainer = MainTheme,
    //Đối tượng trên nền màu chủ đạo
    onPrimaryContainer = Color.Black,
    //Màu nền phần nội dung (trắng)
    background = Color.White,
    //Đối tượng trên background (đen)
    onBackground = Color.Black,
    //Màu nền nội dung cấp 2 (xám sáng) thường dùng cho các card nằm trên background
    secondary = ButtonTheme,
//    // Nội dung trên card
//    onSecondary = Color.Gray,
    //Màu nền nội dung cấp 2 (xám sáng) thường dùng cho các card nằm trên background
    secondaryContainer = secondContainer,
    // Nội dung trên card
    onSecondaryContainer = Color.DarkGray,
    //Màu nền nội dung cấp 3 (xanh mờ) thường dùng cho các component như thông báo chưa xem
    tertiaryContainer = LightTheme

)

@Composable
fun HealthCareSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}