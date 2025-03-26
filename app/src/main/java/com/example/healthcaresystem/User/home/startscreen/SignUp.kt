package com.example.healthcaresystem.User.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthcaresystem.R
import com.example.healthcaresystem.api.RetrofitInstance
import com.example.healthcaresystem.model.SignUpRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_sign_up)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnSignUp = findViewById<TextView>(R.id.signupbtn)
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpSuccess::class.java)
            startActivity(intent) // Chuyển đến SignUpSuccess
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

        val linearLayoutPass= findViewById<LinearLayout>(R.id.linearpassword)
        val passwordInput = linearLayoutPass.findViewById<EditText>(R.id.password)
        val linearLayoutRepass= findViewById<LinearLayout>(R.id.linearrepassword)
        val repasswordInput = linearLayoutRepass.findViewById<EditText>(R.id.repassword)
        val usernameInput = findViewById<EditText>(R.id.username)
        val phoneInput = findViewById<EditText>(R.id.phonenumber)

        val passwordEditText = findViewById<EditText>(R.id.password)
        val passwordEyeIcon = findViewById<ImageView>(R.id.password_eye)
        val repasswordEditText = findViewById<EditText>(R.id.repassword)
        val repasswordEyeIcon = findViewById<ImageView>(R.id.repassword_eye)

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

        // Xử lý sự kiện nhấn vào icon mắt
        passwordEyeIcon.setOnClickListener { togglePasswordVisibility(passwordEditText, passwordEyeIcon) }
        repasswordEyeIcon.setOnClickListener { togglePasswordVisibility(repasswordEditText, repasswordEyeIcon) }


        btnSignUp.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val phoneNumber = phoneInput.text.toString().trim()
            val repassword = repasswordInput.text.toString().trim()


            if (email.isEmpty() || password.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || password!=repassword) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin"+password+" "+repassword, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(username, email, phoneNumber, password)

        }
    }

    private fun registerUser(username: String, email: String, phone:String, password: String) {
        CoroutineScope(Dispatchers.IO).launch { //khởi chạy Coroutine trên background Thread
            try {
                val response = RetrofitInstance.api.signUp(SignUpRequest(username, email, phone, password))

                //chuyển về main thread
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //toast chỉ có thể chạy ở main thread
                        Toast.makeText(this@SignUp, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUp, SignUpSuccess::class.java))
                    } else {
                        // Lấy lỗi từ API
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@SignUp, "Lỗi: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUp, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}