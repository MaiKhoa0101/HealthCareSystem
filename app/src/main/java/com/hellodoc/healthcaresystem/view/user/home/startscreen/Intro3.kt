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
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.view.ui.theme.HealthCareSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Intro3 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                Intro3Screen(
                    onBack = {
                        // finish() will go back to the previous activity in the stack, which should be Intro2
                        finish()
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    },
                    onStart = {
                        startActivity(Intent(this, StartScreen::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun Intro3Screen(onBack: () -> Unit, onStart: () -> Unit) {
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
                // Assuming HelloDocLogo is defined elsewhere or will be added
                // For now, a placeholder Text can be used if HelloDocLogo is not available
                // Text("HelloDoc Logo Placeholder", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                HelloDocLogo(modifier = Modifier.size(200.dp))
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Sức khỏe là vàng",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đặt lịch khám bệnh dễ dàng, theo dõi sức khỏe thông minh và nhận tư vấn từ chuyên gia.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                // Assuming PrimaryButton and SecondaryButton are defined elsewhere or will be added
                PrimaryButton(
                    text = "Bắt đầu ngay",
                    onClick = onStart
                )
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(
                    text = "Quay lại",
                    onClick = onBack
                )
            }
        }
    }
}
