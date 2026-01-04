package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.os.Bundle
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.auth0.android.jwt.JWT
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.view.admin.AdminRoot
import com.hellodoc.healthcaresystem.view.user.home.root.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class Intro1 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)

        setContent {
            HealthCareSystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        HelloDocLogo()
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }
                }

                LaunchedEffect(Unit) {
                    delay(2000)
                    if (token == null) {
                        startActivity(Intent(this@Intro1, Intro2::class.java))
                    } else {
                        try {
                            val jwt = JWT(token)
                            val role = jwt.getClaim("role").asString() ?: "unknown"
                            if (role == "Admin") {
                                startActivity(Intent(this@Intro1, AdminRoot::class.java))
                            } else {
                                startActivity(Intent(this@Intro1, HomeActivity::class.java))
                            }
                        } catch (e: Exception) {
                            startActivity(Intent(this@Intro1, Intro2::class.java))
                        }
                    }
                    finish()
                }
            }
        }
    }
}