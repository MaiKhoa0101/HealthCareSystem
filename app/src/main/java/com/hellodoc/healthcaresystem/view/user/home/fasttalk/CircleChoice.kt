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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResult
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.alignmentAndDirection
import com.hellodoc.healthcaresystem.viewmodel.FastTalkViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircleWordMenu(
    currentWord: String,
    onChoice: (String) -> Unit,
    onExtend: (String) -> Unit // Truyền string key (Top/Left...) hoặc Label để mở rộng
) {

    val viewModel: FastTalkViewModel = hiltViewModel()

    // Collect Data từ ViewModel
    val verbs by viewModel.wordVerbSimilar.collectAsState()
    val nouns by viewModel.wordNounSimilar.collectAsState()
    val adjs  by viewModel.wordSupportSimilar.collectAsState()
    val pros  by viewModel.wordPronounSimilar.collectAsState()

    var bestWord by remember { mutableStateOf("") }

    // ✅ GỌI API KHI TỪ INPUT ĐỔI
    LaunchedEffect(currentWord) {
        if (currentWord.isNotBlank()) {
            viewModel.getWordSimilar(currentWord)
        }
    }

    // ✅ TÍNH BEST WORD KHI DATA ĐỔI (Word có score cao nhất trong tất cả các nhóm)
    LaunchedEffect(verbs, nouns, adjs, pros) {
        val allWords = verbs + nouns + adjs + pros
        println("all word tìm thấy là $allWords")
        bestWord = allWords.maxByOrNull { it.score }?.word ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center
    ) {

        alignmentAndDirection.forEach { item ->

            // Mapping vị trí UI với dữ liệu
            val group = when (item.alignment) {
                "Top" -> nouns       // Trên: Danh từ
                "Left" -> verbs      // Trái: Động từ
                "Right" -> adjs      // Phải: Tính/Trạng từ
                "Bottom" -> pros     // Dưới: Đại từ
                else -> emptyList()
            }

            // Lấy từ có điểm cao nhất trong nhóm để hiển thị
            val content = group.firstOrNull()?.word ?: ""

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
                        // Khi nhấn giữ -> Mở rộng danh sách của nhóm đó
                        onLongClick = { onExtend(item.alignment) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background mũi tên
                Image(
                    painter = painterResource(R.drawable.arrow_area),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .rotate(item.direction)
                )
                // Text hiển thị
                Text(
                    text = content,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // ✅ Nút trung tâm: Hiển thị từ input hoặc từ dự đoán tốt nhất
        CircleButtonWord(
            word = bestWord.ifBlank { currentWord },
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
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angleAnim"
    )

    val rainbowBrush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFFFF00FF), Color(0xFF0000FF), Color(0xFF00FFFF),
            Color(0xFF00FF00), Color(0xFFFFFF00), Color(0xFFFF0000),
            Color(0xFFFFFF00), Color(0xFF00FF00), Color(0xFF00FFFF),
            Color(0xFF0000FF), Color(0xFFFF00FF)
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = -angle }
            .border(width = 5.dp, brush = rainbowBrush, shape = CircleShape)
            .graphicsLayer { rotationZ = angle }
            .background(backgroundColor, shape = CircleShape)
            .clip(CircleShape)
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
    groupWord: List<WordResult> // ✅ Sửa type thành WordResult
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        items(groupWord) { wordItem ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .combinedClickable(
                        onClick = { onChoice(wordItem.word) }, // ✅ Dùng .word
                        onLongClick = { /* giữ lâu nếu cần */ },
                        onDoubleClick = { /* nhấn đôi nếu cần */ }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = wordItem.word, // ✅ Dùng .word
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}