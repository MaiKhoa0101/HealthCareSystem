package com.hellodoc.healthcaresystem.blindview.userblind.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.EmailRequest
import com.hellodoc.healthcaresystem.requestmodel.OtpVerifyRequest
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyOtpActivity : BaseActivity() {

    private lateinit var otpInput: EditText
    private lateinit var verifyOtpButton: Button
    private lateinit var resendOtpText: TextView
    private lateinit var backButton: ImageButton

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp)

        // Nhận email từ Intent
        email = intent.getStringExtra("email") ?: ""

        otpInput = findViewById(R.id.otpInput)
        verifyOtpButton = findViewById(R.id.verifyOtpButton)
        backButton = findViewById(R.id.backButton)

        verifyOtpButton.setOnClickListener {
            val otp = otpInput.text.toString().trim()
            if (otp.length == 6) {
                verifyOtp(email, otp)
            } else {
                Toast.makeText(this, "Vui lòng nhập mã OTP hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }

        val resendOtpText = findViewById<TextView>(R.id.resendOtpText)

        fun startCountdown() {
            resendOtpText.isEnabled = false
            resendOtpText.isClickable = false

            object : CountDownTimer(15000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000
                    val formattedSeconds = String.format("00:%02d", secondsLeft)
                    resendOtpText.text = "Gửi lại ($formattedSeconds)"
                }

                override fun onFinish() {
                    resendOtpText.text = "Gửi lại"
                    resendOtpText.isEnabled = true
                    resendOtpText.isClickable = true
                }
            }.start()
        }

        startCountdown()

        resendOtpText.setOnClickListener {
            resendOtp(email)
            startCountdown()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun verifyOtp(email: String, otp: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.verifyOtp(
                    OtpVerifyRequest(email = email, otp = otp)
                )
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()!!.message
                    Toast.makeText(this@VerifyOtpActivity, message, Toast.LENGTH_SHORT).show()

                    // Chuyển sang màn hình đặt lại mật khẩu
                    val intent = Intent(this@VerifyOtpActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@VerifyOtpActivity, "Xác minh thất bại: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VerifyOtpActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendOtp(email: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.requestOtp(email)
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@VerifyOtpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VerifyOtpActivity, "Gửi lại OTP thất bại: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VerifyOtpActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
