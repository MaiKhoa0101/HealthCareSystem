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
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Word
import com.hellodoc.healthcaresystem.viewmodel.FastTalkViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircleWordMenu(
    currentWord: String,
    onChoice: (String) -> Unit,
    onExtend: (String) -> Unit
) {

    val viewModel: FastTalkViewModel = hiltViewModel()

    val verb by viewModel.wordVerbSimilar.collectAsState()
    val noun by viewModel.wordNounSimilar.collectAsState()
    val adj  by viewModel.wordSupportSimilar.collectAsState()
    val pro  by viewModel.wordPronounSimilar.collectAsState()

    var bestWord by remember { mutableStateOf("") }

    // ✅ GỌI API KHI TỪ ĐỔI
    LaunchedEffect(currentWord) {
        if (currentWord.isNotBlank()) {
            viewModel.getWordSimilar(currentWord)
        }
    }

    // ✅ TÍNH BEST WORD KHI DATA ĐỔI
    LaunchedEffect(verb, noun, adj, pro) {
        val allWords = verb + noun + adj + pro
        bestWord = allWords.maxByOrNull { it.score }?.suggestion ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center
    ) {

        alignmentAndDirection.forEachIndexed { index, item ->

            val group = when (item.alignment) {
                "Top" -> noun
                "Left" -> verb
                "Right" -> adj
                "Bottom" -> pro
                else -> emptyList()
            }

            val content =
                if (group.isNotEmpty())
                    group[index % group.size].suggestion
                else ""

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
                            if (content.isNotBlank()) onChoice(content)
                        },
                        onLongClick = { onExtend(item.alignment) }
                    ),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.arrow_area),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .rotate(item.direction)
                )

                Text(
                    text = content,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ✅ Nút trung tâm luôn là bestWord
        CircleButtonWord(
            word = if (bestWord.isNotBlank()) bestWord else currentWord,
            onClick = {
                if (bestWord.isNotBlank()) onChoice(bestWord)
            }
        )
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
    groupWord: List<Word>
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
                        onClick = { onChoice(word.suggestion) },
                        onLongClick = { /* giữ lâu */ },
                        onDoubleClick = { /* nhấn đôi */ }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word.suggestion,
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