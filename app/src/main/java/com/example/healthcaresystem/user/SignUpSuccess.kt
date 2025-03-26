<<<<<<<< HEAD:app/src/main/java/com/example/healthcaresystem/user/SignUpSuccess.kt
package com.example.healthcaresystem.user
========
package com.example.healthcaresystem.user.home.startscreen
>>>>>>>> ca90ff817dfc39f7bff798a857390860606ccc54:app/src/main/java/com/example/healthcaresystem/user/home/startscreen/SignUpSuccess.kt

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthcaresystem.R
import com.example.healthcaresystem.user.home.startscreen.SignIn

class SignUpSuccess : AppCompatActivity() {
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