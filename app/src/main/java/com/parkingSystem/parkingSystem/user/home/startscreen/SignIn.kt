package com.parkingSystem.parkingSystem.user.home.startscreen
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
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
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
import com.parkingSystem.core.common.activity.BaseActivity
import com.parkingSystem.parkingSystem.admin.AdminRoot
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.requestmodel.FirebaseLoginRequest
import com.parkingSystem.parkingSystem.requestmodel.GoogleLoginRequest
import com.parkingSystem.parkingSystem.staff.StaffActivityScreen
import com.parkingSystem.parkingSystem.user.home.root.HomeActivity
import com.parkingSystem.parkingSystem.user.home.root.showToast

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
                Toast.makeText(this, "Please fill in all information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signInWithFirebase(email, password)
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


    private fun signInWithFirebase(email: String, password: String) {
        showProgressBar()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Lấy idToken từ Firebase user
                    auth.currentUser?.getIdToken(true)
                        ?.addOnCompleteListener { tokenTask ->
                            hideProgressBar()
                            if (tokenTask.isSuccessful) {
                                val idToken = tokenTask.result?.token
                                if (!idToken.isNullOrEmpty()) {
                                    println("Send token to backend")
                                    sendTokenToBackend(idToken)
                                } else {
                                    Toast.makeText(this, "Cannot get tokens!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Cannot get tokens!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    hideProgressBar()
                    Toast.makeText(this, "Email or password is incorrect!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendTokenToBackend(idToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.login(FirebaseLoginRequest(idToken))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val token = loginResponse?.accessToken
                        if (!token.isNullOrEmpty()) {
                            saveToken(token)
                            Toast.makeText(this@SignIn, "Login success!", Toast.LENGTH_SHORT).show()
                            val jwt = JWT(token)
                            val role = jwt.getClaim("role").asString()
                            println ("Role la "+ role)
                            val intent = when (role) {
                                "Admin" -> Intent(this@SignIn, AdminRoot::class.java)
                                "User" -> Intent(this@SignIn, HomeActivity::class.java)
//                                "Staff" -> Intent(this@SignIn, StaffActivityScreen::class.java)
                                else -> {
                                    Toast.makeText(
                                        this@SignIn,
                                        "Vai trò không hợp lệ: $role",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@withContext                                }
                            }
                            startActivity(intent)
                            finish()
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
        else{
            println("Ko ok "+result.resultCode)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account = task.result
            if(account != null){
                Toast.makeText(this, "Có tồn tại tài khoản này", Toast.LENGTH_SHORT).show()
                updateUI(account)
            }
            else{
                Toast.makeText(this, "Không tồn tại tài khoản này", Toast.LENGTH_SHORT).show()
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
                val idToken = account.idToken ?: ""
                val phone = ""

                val request = GoogleLoginRequest(idToken = idToken, phone = phone)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        println("Vao duoc login gg")
                        val response = RetrofitInstance.api.loginGoogle(request)
                        println("Response là: "+ response.toString())
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                val token = loginResponse?.accessToken
                                println("Login thanh cong")
                                if (!token.isNullOrEmpty()) {
                                    saveToken(token)

                                    if (isValidJWTFormat(token)) {
                                        try {
                                            val jwt = JWT(token)
                                            val role = jwt.getClaim("role").asString()
                                            println ("Role la "+ role)
                                            val intent = when (role) {
                                                "Admin" -> Intent(this@SignIn, AdminRoot::class.java)
                                                "User", "Doctor" -> Intent(this@SignIn, HomeActivity::class.java)
                                                else -> {
                                                    Toast.makeText(
                                                        this@SignIn,
                                                        "Vai trò không hợp lệ: $role",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@withContext
                                                }
                                            }

                                            Toast.makeText(this@SignIn, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                            startActivity(intent)
                                            finish()
                                        } catch (e: Exception) {
                                            Toast.makeText(this@SignIn, "Lỗi đọc token: ${e.message}", Toast.LENGTH_SHORT).show()
                                            Log.e("JWT_ERROR", "JWT parsing error", e)
                                        }
                                    } else {
                                        Toast.makeText(this@SignIn, "Token không đúng định dạng JWT", Toast.LENGTH_SHORT).show()
                                        Log.e("JWT_ERROR", "Invalid JWT format: $token")
                                    }
                                } else {
                                    Toast.makeText(this@SignIn, "Token rỗng hoặc null!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                println("Login that bai")

                                Toast.makeText(this@SignIn, "Đăng nhập thất bại: $errorBody", Toast.LENGTH_SHORT).show()
                                Log.e("API_ERROR", "Login failed: $errorBody")
                            }

                            hideProgressBar()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SignIn, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("NETWORK_ERROR", "Connection error", e)
                            hideProgressBar()
                        }
                    }
                }
            } else {
                hideProgressBar()
                Toast.makeText(this, "Không thể đăng nhập. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show()
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