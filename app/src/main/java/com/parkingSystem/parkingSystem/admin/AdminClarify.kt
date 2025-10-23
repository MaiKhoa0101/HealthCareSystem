package com.parkingSystem.parkingSystem.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun TableCell(text: String, isHeader: Boolean = false, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .background(
                if (isHeader) Color(0xFF002E5D)
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) Color.White else Color.Black,
            fontSize = if (isHeader) 14.sp else 13.sp
        )
    }
}
@Composable
fun TableCellImage(imageUrl: String, isHeader: Boolean = false, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "Doctor Avatar",
            modifier = Modifier
                .clip(CircleShape)
                .size(110.dp)
        )
    }
}
