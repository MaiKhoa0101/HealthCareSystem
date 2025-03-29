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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthcaresystem.R
import com.example.healthcaresystem.requestmodel.UpdateUser

@Preview(showBackground = true)
@Composable
fun PreviewReportListScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    Text(text = "Employee manager")
}
@Composable
fun ReportManagerScreen(){
    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Quản lý khiếu nại",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    )
}