package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Intro2 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                Intro2Screen(
                    onNext = {
                        startActivity(Intent(this, Intro3::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun Intro2Screen(onNext: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(60.dp))
                HelloDocLogo(modifier = Modifier.size(200.dp))
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Bác sĩ tận tâm",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Kết nối với những bác sĩ chuyên môn hàng đầu ngay tại nhà của bạn.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            PrimaryButton(
                text = "Tiếp theo",
                onClick = onNext,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}