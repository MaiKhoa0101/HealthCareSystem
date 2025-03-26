package com.example.healthcaresystem.User

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.auth0.android.jwt.JWT
import com.example.healthcaresystem.R
import com.example.healthcaresystem.User.home.startscreen.SignIn

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Lấy token từ SharedPreferences
        val token = getToken()
        if (token != null) {
            val userInfo = decodeJWT(token)
            val userName = userInfo?.get("name") as? String ?: "Người dùng"
            val role = userInfo?.get("role") as? String ?: "Người dùng"
            userNameTextView.text = "Xin chào, $role $userName"
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        // Xử lý đăng xuất
        logoutButton.setOnClickListener {
            clearToken()
            navigateToLogin()
        }
    }

    // Lấy token từ SharedPreferences
    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    // Giải mã JWT để lấy thông tin user
    private fun decodeJWT(token: String): Map<String, Any>? {
        return try {
            val jwt = JWT(token)
            mapOf(
                "userId" to (jwt.getClaim("userId").asString() ?: ""),
                "email" to (jwt.getClaim("email").asString() ?: ""),
                "name" to (jwt.getClaim("name").asString() ?: "")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Xóa token khi đăng xuất
    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("access_token").apply()
    }

    // Chuyển về màn hình đăng nhập sau khi đăng xuất
    private fun navigateToLogin() {
        val intent = Intent(this, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
