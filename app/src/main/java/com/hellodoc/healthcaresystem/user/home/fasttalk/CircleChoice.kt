package com.hellodoc.healthcaresystem.user.home.fasttalk

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircleWordMenu(onChoice: (String) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        var count = 1
        alignmentAndDirection.forEach { item ->
            val content by remember { mutableStateOf(
                if (item.alignment=="Top"){
                    listWordUp.get(0)
                }
                else if (item.alignment=="Left"){
                    listWordsLeft.get(0)
                }
                else if (item.alignment=="Bottom"){
                    listWordDown.get(0)
                }
                else{
                    listWordsRight.get(0)
                }
            ) }
            // Xác định góc dựa theo alignment
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
                    .size(150.dp)
                    .clickable{
                        println("Bấm chọn: "+ content)
                        onChoice(content)
                    },
                contentAlignment = Alignment.Center
            ) {
                // Ảnh mũi tên
                Image(
                    painter = painterResource(id = R.drawable.arrow_area),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(item.direction),
                    contentScale = ContentScale.Fit
                )

                // Text đè giữa mũi tên
                Text(
                    text = content,
                    color = Color.Black,
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
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFFB388FF),
                            Color(0xFF81D4FA),
                            Color(0xFFB9F6CA),
                            Color(0xFFFF8A80)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tôi",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
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
    "Hủ tiếu"
)
val listWordDown = listOf(
    "Tôi",
    "Mình",
    "Chú",
    "Em",
    "Anh"

)