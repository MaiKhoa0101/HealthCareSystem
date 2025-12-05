package com.hellodoc.healthcaresystem.view.user.home.fasttalk

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.FastTalkViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircleWordMenu(
    onChoice: (String) -> Unit,
    onExtend: (String) -> Unit
) {
    val fastTalkViewModel: FastTalkViewModel = hiltViewModel()

    val verb = fastTalkViewModel.wordVerbSimilar.collectAsState()
    val noun = fastTalkViewModel.wordNounSimilar.collectAsState()
    val adj = fastTalkViewModel.wordAdjectiveSimilar.collectAsState()
    val pro = fastTalkViewModel.wordPronounSimilar.collectAsState()
    val bestWord = remember { mutableStateOf("")}
    var currentWord by remember { mutableStateOf("tôi") }

    // Gọi API khi đổi từ
    LaunchedEffect(currentWord) {
        fastTalkViewModel.getWordSimilar(currentWord)
        //Best word là từ có điểm cao nhất trong các nhóm
        val allWords = buildList {
            addAll(verb.value)
            addAll(noun.value)
            addAll(adj.value)
            addAll(pro.value)
        }

        // Lấy từ có trọng số cao nhất
        bestWord.value = allWords.maxByOrNull { it.score }?.suggestion ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center
    ) {

        var count = 0

        alignmentAndDirection.forEach { item ->

            val groupWord = when (item.alignment) {
                "Top" -> noun    // Danh từ
                "Left" -> verb   // Động từ
                "Bottom" -> pro  // Chủ từ
                "Right" -> adj   // Tính từ
                else -> null
            }

            // Nếu không có kết quả → để trống
            val content =
                if (groupWord != null && groupWord.value.isNotEmpty())
                    groupWord.value[count % groupWord.value.size].suggestion
                else
                    ""

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
                        onClick = {
                            if (content.isNotBlank()) {
                                onChoice(content)
                                currentWord = content
                            }
                        },
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
                    text = content,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            count++
        }

        // Nút trung tâm
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircleButtonWord(
                onClick = {
                    onChoice(it)
                    currentWord = it
                },
                word = currentWord,
                modifier = Modifier.align(Alignment.Center)
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