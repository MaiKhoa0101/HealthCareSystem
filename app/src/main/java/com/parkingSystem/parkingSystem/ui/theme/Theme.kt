package com.parkingSystem.parkingSystem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.google.firebase.annotations.concurrent.Background
import kotlin.math.*

data class GradientTheme(
    val primary: Brush
)
val LocalGradientTheme = staticCompositionLocalOf {
    GradientTheme(
        primary = Brush.linearGradient(
            listOf(
                Color(0xFF002E5D),
                Color(0xFFFFFFFF)
            )
        )
    )
}

private val LightGradientTheme = GradientTheme(
    primary = Brush.linearGradient(
        listOf(Color(0xFF002E5D), Color(0xFFFFFFFF))
    )
)

private val DarkGradientTheme = GradientTheme(
    primary = Brush.linearGradient(
        listOf(Color(0xFF141414), Color(0xFF2A2A2A))
    )
)

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
    error = CustomRed,
    tertiary = BoxGrey,
    surfaceVariant = BoxLightGrey,
    errorContainer = LightRedCustom,
    outlineVariant = AmberCustom,
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
    tertiaryContainer = LightTheme,
    error = CustomRed,
    tertiary = LightBlueCustom,
    surfaceVariant = MidGrayCustom,
    errorContainer = LightRedCustom,
    outlineVariant = AmberCustom,
)

@Composable
fun ParkingSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
    val gradientScheme = if (darkTheme) DarkGradientTheme else LightGradientTheme

    CompositionLocalProvider(LocalGradientTheme provides gradientScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object AppGradients {
    fun angledLinearGradient(
        angleDeg: Float,
        colors: List<Color>,
        width: Float = 1000f,
        height: Float = 1000f
    ): Brush {
        // Đổi góc sang radian
        val angleRad = Math.toRadians(angleDeg.toDouble())
        // Tính toạ độ vector hướng theo góc
        val x = cos(angleRad)
        val y = sin(angleRad)

        val start = Offset(width * (0.5f - x.toFloat() / 2), height * (0.5f - y.toFloat() / 2))
        val end = Offset(width * (0.5f + x.toFloat() / 2), height * (0.5f + y.toFloat() / 2))

        return Brush.linearGradient(
            colors = colors,
            start = start,
            end = end
        )
    }

    val Background = angledLinearGradient(
        angleDeg = 135f,
        colors = listOf(
            Color.White,
            MainColor
        )
    )
}