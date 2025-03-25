package com.example.healthcaresystem.User

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthcaresystem.R

class StartScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.startscreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signInButton = findViewById<TextView>(R.id.btnSignIn)
        signInButton.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
        }
        val signUpButton = findViewById<TextView>(R.id.btnSignUp)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
        }
        val returnButton = findViewById<ImageButton>(R.id.returnButton)
        returnButton.setOnClickListener {
            val intent = Intent(this, Intro3::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) // Slide left when going back
        }

    }
}