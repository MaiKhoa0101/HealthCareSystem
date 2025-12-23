package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.requestmodel.SignUpRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ThirdSignUp : BaseActivity() {
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var phone: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.third_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.third_sign_up)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get data from intent
        email = intent.getStringExtra("email") ?: ""
        username = intent.getStringExtra("username") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        password = intent.getStringExtra("password") ?: ""

        val roleGroup = findViewById<RadioGroup>(R.id.role_group)
        val btnSignUp = findViewById<Button>(R.id.signupbtn)
        val returnButton = findViewById<ImageButton>(R.id.returnButton)

        returnButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        btnSignUp.setOnClickListener {
            val selectedId = roleGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn vai trò của bạn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = when (selectedId) {
                R.id.role_user -> "User"
                R.id.role_blind -> "Blind"
                R.id.role_deaf -> "Deaf"
                R.id.role_mute -> "Mute"
                else -> "User"
            }

            registerUser(username, phone, password, role)
        }
    }

    private fun registerUser(username: String, phone: String, password: String, role: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.signUp(SignUpRequest(username, email, phone, password, role))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ThirdSignUp, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ThirdSignUp, SignUpSuccess::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@ThirdSignUp, "Lỗi: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ThirdSignUp, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
