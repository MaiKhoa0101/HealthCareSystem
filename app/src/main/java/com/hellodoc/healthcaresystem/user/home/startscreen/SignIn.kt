package com.hellodoc.healthcaresystem.user.home.startscreen

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.requestmodel.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.auth0.android.jwt.JWT
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.admin.AdminRoot
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.SignUpRequest
import com.hellodoc.healthcaresystem.requestmodel.genToken
import com.hellodoc.healthcaresystem.user.home.root.HomeActivity
import com.hellodoc.healthcaresystem.user.home.root.showToast

class SignIn : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_sign_in)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val nutdangki = findViewById<TextView>(R.id.signuplink)
        nutdangki.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val nutdangnhapgoogle = findViewById<LinearLayout>(R.id.googleSignInButton)

        val returnButton = findViewById<ImageButton>(R.id.returnButton)
        returnButton.setOnClickListener {
            val intent = Intent(this, StartScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val emailInput = findViewById<EditText>(R.id.email)
        val passwordInput = findViewById<EditText>(R.id.pass)
        val nutdangnhap = findViewById<Button>(R.id.button)

        nutdangnhap.setOnClickListener{
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userLogin(email, password)
        }

        nutdangnhapgoogle.setOnClickListener {
            signInWithGoogle()
        }

        var isPasswordVisible = false
        val passwordEditText = findViewById<EditText>(R.id.pass)
        val togglePasswordBtn = findViewById<ImageView>(R.id.togglePasswordVisibility)

        togglePasswordBtn.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordBtn.setImageResource(R.drawable.baseline_disabled_visible_24)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordBtn.setImageResource(R.drawable.baseline_remove_red_eye_24)
            }

            passwordEditText.setSelection(passwordEditText.text.length)
        }

        val forgotPasswordBtn = findViewById<TextView>(R.id.forgot_password)
        forgotPasswordBtn.setOnClickListener {
            val intent = Intent(this@SignIn, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun userLogin(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(email, password))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val token = loginResponse?.accessToken

                        if (!token.isNullOrEmpty()) {
                            saveToken(token)

                            try {
                                val jwt = JWT(token)
                                val role = jwt.getClaim("role").asString()

                                val intent = when (role) {
                                    "admin" -> Intent(this@SignIn, AdminRoot::class.java)
                                    "user" -> Intent(this@SignIn, HomeActivity::class.java)
                                    "doctor" -> Intent(this@SignIn, HomeActivity::class.java)
                                    else -> {
                                        Toast.makeText(this@SignIn, "Vai trò không hợp lệ!", Toast.LENGTH_SHORT).show()
                                        return@withContext
                                    }
                                }

                                Toast.makeText(this@SignIn, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            } catch (e: Exception) {
                                Toast.makeText(this@SignIn, "Không thể đọc thông tin người dùng từ token", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@SignIn, "Token không hợp lệ!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@SignIn, "Đăng nhập thất bại: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignIn, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                updateUI(account)
            }
        } else {
            showToast(this, "SignIn Failed, Try again later")
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isNewUser = task.result?.additionalUserInfo?.isNewUser == true

                if (isNewUser) {
                    // Tài khoản mới -> Gọi API lưu vào database
                    val signupRequest = SignUpRequest(
                        email = account.email ?: "",
                        name = account.displayName ?: "Google User",
                        phone = "",
                        password = ""
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitInstance.api.loginGoogle(signupRequest)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    val signupResponse = response.body()
                                    startActivity(Intent(this@SignIn, HomeActivity::class.java))
                                    finish()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Toast.makeText(this@SignIn, "Tạo tài khoản thất bại: $errorBody", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@SignIn, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    val tokenReq = genToken(email = account.email ?: "")
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitInstance.api.generateToken(tokenReq)
                            println("response $response")

                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    val loginResponse = response.body()
                                    println("login res $loginResponse")
                                    val token = loginResponse?.accessToken

                                    // Debug: In ra token để kiểm tra
                                    Log.d("TOKEN_DEBUG", "Received token: $token")

                                    if (!token.isNullOrEmpty()) {
                                        saveToken(token)

                                        try {
                                            // Kiểm tra token format trước khi parse
                                            if (isValidJWTFormat(token)) {
                                                val jwt = JWT(token)
                                                val role = jwt.getClaim("role").asString()

                                                // Debug: In ra role
                                                Log.d("ROLE_DEBUG", "User role: $role")

                                                val intent = when (role) {
                                                    "admin" -> Intent(this@SignIn, AdminRoot::class.java)
                                                    "user" -> Intent(this@SignIn, HomeActivity::class.java)
                                                    "doctor" -> Intent(this@SignIn, HomeActivity::class.java)
                                                    else -> {
                                                        Toast.makeText(this@SignIn, "Vai trò không hợp lệ: $role", Toast.LENGTH_SHORT).show()
                                                        return@withContext
                                                    }
                                                }

                                                Toast.makeText(this@SignIn, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(this@SignIn, "Token không đúng định dạng JWT", Toast.LENGTH_SHORT).show()
                                                Log.e("JWT_ERROR", "Invalid JWT format: $token")
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(this@SignIn, "Lỗi đọc token: ${e.message}", Toast.LENGTH_SHORT).show()
                                            Log.e("JWT_ERROR", "JWT parsing error", e)
                                        }
                                    } else {
                                        Toast.makeText(this@SignIn, "Token rỗng hoặc null!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Toast.makeText(this@SignIn, "Đăng nhập thất bại: $errorBody", Toast.LENGTH_SHORT).show()
                                    Log.e("API_ERROR", "Generate token failed: $errorBody")
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@SignIn, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("NETWORK_ERROR", "Connection error", e)
                            }
                        }
                    }
                }
            } else {
                hideProgressBar()
                showToast(this, "Can't login currently. Try after sometime")
            }
        }
    }

    // Thêm function helper để validate JWT format
    private fun isValidJWTFormat(token: String): Boolean {
        return try {
            val parts = token.split(".")
            parts.size == 3 && parts.all { it.isNotEmpty() }
        } catch (e: Exception) {
            false
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("access_token", token)
            .apply()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}