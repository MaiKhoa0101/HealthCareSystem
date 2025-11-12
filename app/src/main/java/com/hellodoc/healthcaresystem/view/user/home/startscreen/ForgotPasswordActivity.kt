package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.EmailRequest
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var emailInput: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var returnBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        emailInput = findViewById(R.id.email)
        sendOtpButton = findViewById(R.id.sendOtpBtn)
        returnBtn = findViewById(R.id.returnButton)

        sendOtpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (isValidEmail(email)) {
                sendOtpToEmail(email)
            } else {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
            }
        }

        returnBtn.setOnClickListener {
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendOtpToEmail(email: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.requestOtp(email)
                if (response.isSuccessful && response.body() != null) {
                    val otpResponse = response.body()!!
                    Toast.makeText(this@ForgotPasswordActivity, otpResponse.message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ForgotPasswordActivity, VerifyOtpActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Gửi OTP thất bại: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ForgotPasswordActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
