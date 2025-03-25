package com.example.healthcaresystem.User

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthcaresystem.R

class Intro2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val nextButton = findViewById<TextView>(R.id.btnNext)
        nextButton.setOnClickListener {
            val intent = Intent(this, Intro3::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
        }

    }
}