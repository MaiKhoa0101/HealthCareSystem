package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Intro2 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Intro2", "onCreate called")
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