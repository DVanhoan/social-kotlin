package com.hoan.client

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.hoan.client.constant.Constants
import com.hoan.client.databinding.ActivityLoginBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.request.JwtRequest
import com.hoan.client.network.response.JwtResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var inputMethodManager: InputMethodManager? = null

    private var isUsernameCorrect: Boolean = false
    private var isPasswordCorrect: Boolean = false
    private val passwordRegex: Regex =
        Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
    private val sharedPrefName = "user_shared_preference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        val intentUsername: String? = intent.getStringExtra("username")
        val intentPassword: String? = intent.getStringExtra("password")

        if (intentUsername != null && intentPassword != null) {
            login(intentUsername, intentPassword)
        }

        binding.btnLogin.setOnClickListener {
            val usernameOrEmail = binding.etUsernameOrEmail.text.toString()
            val password = binding.etPassword.text.toString()
            login(usernameOrEmail, password)
        }

        binding.btnLogin.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputMethodManager == null) {
                    inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                }
                inputMethodManager?.hideSoftInputFromWindow(binding.btnLogin.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.etUsernameOrEmail.addTextChangedListener { text ->
            isUsernameCorrect = text.toString().length >= 4
            toggleLoginButtonState()
        }

        binding.etPassword.addTextChangedListener { text ->
            isPasswordCorrect =
                text.toString().length >= 8 && text.toString().matches(passwordRegex)
            toggleLoginButtonState()
        }

        binding.logo.setOnClickListener {
            login("nguyen@gmail.com", "Nguyen1234")
        }
    }

    private fun toggleLoginButtonState() {
        binding.btnLogin.isEnabled = isUsernameCorrect && isPasswordCorrect
        binding.btnLogin.isClickable = binding.btnLogin.isEnabled
        val tintColor = if (binding.btnLogin.isEnabled)
            ContextCompat.getColor(this, R.color.primary)
        else
            ContextCompat.getColor(this, R.color.light_grey)
        binding.btnLogin.background.setTint(tintColor)
    }

    private fun login(usernameOrEmail: String, password: String) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.btnLogin.windowToken, 0)

        val jwtRequest = JwtRequest(usernameOrEmail, password)
        val call: Call<JwtResponse> = RetrofitInstance.userService.login(jwtRequest)
        call.enqueue(object : Callback<JwtResponse> {
            override fun onResponse(call: Call<JwtResponse>, response: Response<JwtResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    loginSuccess(response.code(), response.body()!!)
                } else {
                    loginError(response.code(), Exception("Login failed: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<JwtResponse>, t: Throwable) {
                loginError(500, t)
            }
        })
    }

    private fun loginSuccess(statusCode: Int, responseBody: JwtResponse) {
        Log.d("LOGIN_SUCCESSFUL", "Token: ${responseBody.jwt}")
        saveUserDetails(responseBody)
        startActivity(Intent(this, FeedActivity::class.java))
        finish()
    }

    private fun saveUserDetails(jwtResponse: JwtResponse) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong("userId", jwtResponse.user.id)
        editor.putString("username", jwtResponse.user.username)
        editor.putString("email", jwtResponse.user.email)
        editor.putString("jwt", jwtResponse.jwt)

        val expirationTime = System.currentTimeMillis() + jwtResponse.expires_in.toLong() * 1000L
        editor.putLong("expiration_time", expirationTime)
        editor.apply()
    }


    private fun loginError(statusCode: Int, e: Throwable) {
        val errorMessage = when (statusCode) {
            400 -> "Wrong credentials"
            500 -> "Something unexpected happened"
            else -> "Error: $statusCode"
        }
        Constants.showErrorSnackbar(this, layoutInflater, errorMessage)
        e.printStackTrace()
    }
}
