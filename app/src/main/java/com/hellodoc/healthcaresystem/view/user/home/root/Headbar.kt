package com.hellodoc.healthcaresystem.view.user.home.root

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

fun shortenUserName(fullName: String): String {
    val parts = fullName.trim().split("\\s+".toRegex())
    return if (parts.size >= 2) {
        val firstInitial = parts.first().first().uppercaseChar()
        val lastName = parts.last()
        "$firstInitial. $lastName"
    } else {
        fullName // không cần rút gọn nếu chỉ có 1 từ
    }
}

@Composable
fun Headbar() {
    val context = LocalContext.current
    val viewModel: UserViewModel = hiltViewModel()
    val you by viewModel.user.collectAsState()
    var userName by remember { mutableStateOf("Người dùng") }
    var role by remember { mutableStateOf("Người dùng") }

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
        role = viewModel.getUserAttribute("role",context)
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Doctor Icon",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val shortName = you?.name.toString().let { shortenUserName(it) }

                Text(
                    text = "Xin chào\n$shortName",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

fun truncateName(name: String, maxLength: Int): String {
    return if (name.length > maxLength) {
        name.take(maxLength) + "..."
    } else {
        name
    }
}