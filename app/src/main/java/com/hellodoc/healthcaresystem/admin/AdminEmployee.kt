package com.hellodoc.healthcaresystem.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun PreviewEmployeeListScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    Text(text = "Employee manager")
}
@Composable
fun EmployeeManagerScreen(){
    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Quản lý nhân sự",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    )
}