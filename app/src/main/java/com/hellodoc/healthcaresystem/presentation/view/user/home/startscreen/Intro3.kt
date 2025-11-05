package com.hellodoc.healthcaresystem.presentation.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R

class Intro3 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backButton = findViewById<TextView>(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, Intro2::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) // Slide left when going back
        }

        val nextButton = findViewById<TextView>(R.id.btnNext)
        nextButton.setOnClickListener {
            val intent = Intent(this, StartScreen::class.java)
            startActivity(intent)
        }

    }
}