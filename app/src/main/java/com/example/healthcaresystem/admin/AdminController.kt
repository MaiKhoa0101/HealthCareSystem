package com.example.healthcaresystem.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.example.healthcaresystem.viewmodel.UserViewModel
import com.example.healthcaresystem.responsemodel.GetUser
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthcaresystem.R
import com.example.healthcaresystem.requestmodel.UpdateUser

@Preview(showBackground = true)
@Composable
fun PreviewControllerListScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    ControllerManagerScreen()
}
@Composable
fun ControllerManagerScreen() {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(10.dp),
            text = "Trang chủ / Bảng điều khiển",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Grid of Cards
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            // Row 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                InfoCard(
                    value = "150",
                    title = "ca ghi nhan mac benh gay",
                    backgroundColor = Color(0xFF00BCD4), // Cyan
                    icon = R.drawable.submit_arrow, // Replace with your arrow icon
                    modifier = Modifier.padding(2.dp)
                )
                InfoCard(
                    value = "53%",
                    title = "Thang ten Phuong la gay",
                    backgroundColor = Color(0xFF4CAF50), // Cyan
                    icon = R.drawable.submit_arrow, // Replace with your arrow icon
                    modifier = Modifier.padding(2.dp)
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                InfoCard(
                    value = "44",
                    title = "Thang 4 la loi noi xao cua em",
                    backgroundColor = Color(0xFFFFCB2B), // Cyan
                    icon = R.drawable.submit_arrow, // Replace with your arrow icon
                    modifier = Modifier.padding(2.dp)
                )
                InfoCard(
                    value = "53",
                    title = "Hoang Sa va Truong Sa la cua Viet Nam",
                    backgroundColor = Color(0xFFF44336), // Cyan
                    icon = R.drawable.submit_arrow, // Replace with your arrow icon
                    modifier = Modifier.padding(2.dp)
                )

            }
        }
    }
}
@Composable
fun InfoCard(
    value: String,
    title: String,
    backgroundColor: Color,
    icon: Int, // Resource ID for the arrow icon
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(200.dp)
            .background(
                backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.height(100.dp)
        ) {
            // Value text
            Text(
                text = value,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Title text
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold            )

            // "More info" button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "More info",
                    fontSize = 10.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CardComponent(
    value: String,
    title: String,
    backgroundColor: Color,
    icon: Int, // Resource ID for the icon
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(2f) // Ensures the card is rectangular
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .height(30.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section with Value and Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White
            )

            // "More info" button
            Text(
                text = "More info ➡",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.End)
                    .background(
                        Color.Black.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
