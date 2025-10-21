package com.parkingSystem.parkingSystem.user.home.startscreen

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import com.parkingSystem.core.common.activity.BaseActivity
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.admin.AdminRoot
import com.parkingSystem.parkingSystem.user.home.root.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

class Intro1 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)
        setContentView(R.layout.activity_intro1)

        val gifView = findViewById<ImageView>(R.id.gifImageView)

        val imageLoader = ImageLoader.Builder(this)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val request = ImageRequest.Builder(this)
            .data(R.drawable.gifforcar)
            .target(gifView)
            .build()

        imageLoader.enqueue(request)

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