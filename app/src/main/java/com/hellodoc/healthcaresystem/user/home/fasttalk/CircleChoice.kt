package com.hellodoc.healthcaresystem.user.home.fasttalk

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircleWordMenu(
    onChoice: (String) -> Unit,
    onExtend: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center
    ) {
        var count = 1

        alignmentAndDirection.forEach { item ->
            val groupWord = when (item.alignment) {
                "Top" -> listWordUp
                "Left" -> listWordsLeft
                "Bottom" -> listWordDown
                "Right" -> listWordsRight
                else -> emptyList()
            }

            val content = groupWord.firstOrNull() ?: ""

            Box(
                modifier = Modifier
                    .align(
                        when (item.alignment) {
                            "Top" -> Alignment.TopCenter
                            "Left" -> Alignment.CenterStart
                            "Right" -> Alignment.CenterEnd
                            "Bottom" -> Alignment.BottomCenter
                            else -> Alignment.Center
                        }
                    )
                    .size(120.dp)
                    .combinedClickable(
                        onClick = { onChoice(content) },
                        onLongClick = { onExtend(item.alignment) },
                        onDoubleClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_area),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .rotate(item.direction),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "$content$count",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            count++
        }

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircleButtonWord(
                onClick = { onChoice(it) },
                modifier = Modifier.align(Alignment.Center),
                word = "tôi"
            )
        }
    }
}


@Composable
fun CircleButtonWord(
    onClick: (String) -> Unit,
    word: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFE3F2FD),
    size: Dp = 120.dp
) {
    // Hiệu ứng xoay gradient
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing), // 6s xoay 1 vòng
            repeatMode = RepeatMode.Restart
        ),
        label = "angleAnim"
    )

    // Gradient cầu vồng khép kín
    val rainbowBrush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFFFF00FF), // tím magenta
            Color(0xFF0000FF), // xanh dương
            Color(0xFF00FFFF), // cyan
            Color(0xFF00FF00), // lục
            Color(0xFFFFFF00), // vàng
            Color(0xFFFF0000), // đỏ
            Color(0xFFFFFF00), // vàng
            Color(0xFF00FF00), // lục
            Color(0xFF00FFFF), // cyan
            Color(0xFF0000FF), // xanh dương
            Color(0xFFFF00FF)  // trở lại tím magenta
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = -angle // Xoay gradient
            }
            .border(
                width = 5.dp,
                brush = rainbowBrush,
                shape = CircleShape
            )
            .graphicsLayer {
                rotationZ = angle // Giữ phần trong đứng yên
            }
            .background(backgroundColor, shape = CircleShape)
            .clip(
                CircleShape
            )
            .clickable { onClick(word) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtendingChoice(
    onChoice: (String) -> Unit,
    groupWord: List<String>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        items (groupWord) { word ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .combinedClickable(
                        onClick = { onChoice(word) },
                        onLongClick = { /* giữ lâu */ },
                        onDoubleClick = { /* nhấn đôi */ }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }


}




data class AlignmentAndDirection(
    val alignment: String,
    val direction: Float
)
// Danh sách cung (hướng và vị trí)
val alignmentAndDirection = listOf(
    AlignmentAndDirection(
        alignment = "Top",
        direction = 90f
    ),
    AlignmentAndDirection(
        alignment = "Left",
        direction = 0f
    ),
    AlignmentAndDirection(
        alignment = "Right",
        direction = 180f
    ),
    AlignmentAndDirection(
        alignment = "Bottom",
        direction = 270f
    ),
)

val listWordsLeft = listOf(
    "ăn",
    "uống",
    "xơi"
)

val listWordsRight = listOf(
    "vừa",
    "mới",
    "đã",
    "sắp"
)
val listWordUp = listOf(
    "cơm",
    "canh",
    "cá",
    "hủ tiếu"
)
val listWordDown = listOf(
    "Tôi",
    "Mình",
    "Chú",
    "Em",
    "Anh"

)