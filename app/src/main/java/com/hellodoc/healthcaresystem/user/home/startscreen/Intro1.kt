package com.example.healthcaresystem.user.home.startscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import com.example.healthcaresystem.R

class Intro1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPref.getBoolean("isFirstRun", true)
        setContentView(R.layout.activity_intro1)
        sharedPref.edit().putBoolean("isFirstRun", true).apply()
        if (isFirstRun) {
            // Nếu là lần đầu, hiển thị Intro1 rồi chuyển đến SignIn
            sharedPref.edit().putBoolean("isFirstRun", false).apply()
            setContentView(R.layout.activity_intro1)

            // Sau 3 giây chuyển đến SignIn (hoặc có nút chuyển)
            window.decorView.postDelayed({
                startActivity(Intent(this, Intro2::class.java))
                finish() // Đóng Intro1 để không quay lại được
            }, 3000)

        } else {
            // Nếu đã mở app trước đó, chuyển thẳng đến SignIn
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}