package com.hellodoc.healthcaresystem.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R

class SignUpSuccess : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup_success)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.success)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val nextButton = findViewById<TextView>(R.id.btnSignIn)
        nextButton.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
        }
    }
}