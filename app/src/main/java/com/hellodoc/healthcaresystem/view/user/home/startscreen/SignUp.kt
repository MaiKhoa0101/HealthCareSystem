package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.EmailRequest
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
//import com.example.healthcaresystem.user.SignIn
//import com.example.healthcaresystem.user.SignUpSuccess
import kotlinx.coroutines.launch


class SignUp : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.first_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_sign_up)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNextPage = findViewById<TextView>(R.id.signinlink)
        btnNextPage.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
        }
        val returnButton = findViewById<ImageButton>(R.id.returnButton)
        returnButton.setOnClickListener {
            val intent = Intent(this, StartScreen::class.java)
            startActivity(intent) // Chuyển đến SecondActivity
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) // Slide left when going back
        }

        val emailInput = findViewById<EditText>(R.id.email)

        val btnSignUp = findViewById<TextView>(R.id.signupbtn)
        btnSignUp.setOnClickListener {
            val email = emailInput.text.toString().trim()


            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendOtpToEmailForSignUp(email)
        }
    }

    private fun sendOtpToEmailForSignUp(email: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.requestOtpSignUp(EmailRequest(email))
                if (response.isSuccessful && response.body() != null) {
                    val otpResponse = response.body()!!
                    Toast.makeText(this@SignUp, otpResponse.message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SignUp, VerifyOtpSignUpAcctivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp, "Gửi OTP thất bại: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignUp, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}