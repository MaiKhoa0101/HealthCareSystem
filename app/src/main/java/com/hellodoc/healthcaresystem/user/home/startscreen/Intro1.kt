package com.hellodoc.healthcaresystem.user.home.startscreen

import android.content.Context
import android.os.Bundle
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.admin.AdminRoot
import com.hellodoc.healthcaresystem.user.home.root.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Intro1 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)
        setContentView(R.layout.activity_intro1)
        lifecycleScope.launch {
            delay(1000)
            if (token == null) {
                startActivity(Intent(this@Intro1, Intro2::class.java))
            } else {
                val jwt = JWT(token)
                val role = jwt.getClaim("role").asString() ?: "unknown"
                if (role == "Admin") {
                    startActivity(Intent(this@Intro1, AdminRoot::class.java))
                } else {
                    startActivity(Intent(this@Intro1, HomeActivity::class.java))
                }
            }
            finish()
        }

    }
}