package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.requestmodel.SignUpRequest
import dagger.hilt.android.AndroidEntryPoint
//import com.example.healthcaresystem.user.SignIn
//import com.example.healthcaresystem.user.SignUpSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SecondSignUp : BaseActivity() {
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sub_sign_up)) { v, insets ->
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

        email = intent.getStringExtra("email") ?: ""
        val usernameInput = findViewById<EditText>(R.id.username)
        val phoneInput = findViewById<EditText>(R.id.phonenumber)
        val passwordInput = findViewById<EditText>(R.id.password)
        val repasswordInput = findViewById<EditText>(R.id.repassword)

        val passwordEye = findViewById<ImageView>(R.id.password_eye)
        val repasswordEye = findViewById<ImageView>(R.id.repassword_eye)



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
            togglePasswordVisibility(passwordInput, passwordEye)
        }
        repasswordEye.setOnClickListener {
            togglePasswordVisibility(repasswordInput, repasswordEye)
        }


        btnSignUp.setOnClickListener {
            val password = passwordInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val phoneNumber = phoneInput.text.toString().trim()
            val repassword = repasswordInput.text.toString().trim()


            if (password.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || repassword.isEmpty() ) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password!=repassword) {
                Toast.makeText(this, "Mật khẩu nhập lại không trùng khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển sang màn hình chọn role
            val intent = Intent(this, ThirdSignUp::class.java).apply {
                putExtra("email", email)
                putExtra("username", username)
                putExtra("phone", phoneNumber)
                putExtra("password", password)
            }
            startActivity(intent)
        }
    }
}