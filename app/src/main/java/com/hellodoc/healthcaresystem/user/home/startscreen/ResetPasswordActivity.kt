package com.hellodoc.healthcaresystem.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.ResetPasswordRequest
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class ResetPasswordActivity : BaseActivity() {

    private lateinit var newPasswordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var backButton: ImageButton

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)

        // Lấy email từ intent
        email = intent.getStringExtra("email") ?: ""

        newPasswordInput = findViewById(R.id.newPasswordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        backButton = findViewById(R.id.backButton)

        resetPasswordButton.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (newPassword.length < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetPassword(email, newPassword)
        }

        val passwordEye = findViewById<ImageView>(R.id.togglePasswordVisibility)
        val repasswordEye = findViewById<ImageView>(R.id.toggleRePasswordVisibility)

        fun togglePasswordVisibility(editText: EditText, eyeIcon: ImageView) {
            if (editText.transformationMethod is PasswordTransformationMethod) {
                // Hiển thị mật khẩu
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                eyeIcon.setImageResource(R.drawable.baseline_disabled_visible_24) // Mắt bị gạch
            } else {
                // Ẩn mật khẩu
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                eyeIcon.setImageResource(R.drawable.baseline_remove_red_eye_24) // Mắt thường
            }
            // Giữ con trỏ ở cuối chuỗi
            editText.setSelection(editText.text.length)
        }

        passwordEye.setOnClickListener {
            togglePasswordVisibility(newPasswordInput, passwordEye)
        }
        repasswordEye.setOnClickListener {
            togglePasswordVisibility(confirmPasswordInput, repasswordEye)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun resetPassword(email: String, newPassword: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.resetPassword(
                    ResetPasswordRequest(email = email, newPassword = newPassword)
                )
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@ResetPasswordActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@ResetPasswordActivity, ResetPasswordSuccessActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResetPasswordActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
