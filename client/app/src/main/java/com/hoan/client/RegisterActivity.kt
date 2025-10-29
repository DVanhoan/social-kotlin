package com.hoan.client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.hoan.client.constant.Constants
import com.hoan.client.databinding.ActivityRegisterBinding
import com.hoan.client.network.request.RegisterRequest
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerRequest: RegisterRequest

    private var isUsernameCorrect: Boolean = false
    private var isPasswordCorrect: Boolean = false
    private var isFullNameCorrect: Boolean = false
    private var isEmailCorrect: Boolean = false

    private val passwordRegex: Regex =
        Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
    private val emailRegex: Regex =
        Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val fullName = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()

            registerRequest = RegisterRequest(username, password, fullName, email)
            register(registerRequest)
        }

        binding.btnSwitchToLogin.setOnClickListener {
            finish()
        }

        binding.etUsername.addTextChangedListener { text ->
            if (text.toString().length < 4 || text.toString().length > 16) {
                binding.etUsername.setTextColor(ContextCompat.getColor(baseContext, R.color.red))
                isUsernameCorrect = false
            } else {
                binding.etUsername.setTextColor(ContextCompat.getColor(baseContext, R.color.dark_grey))
                isUsernameCorrect = true
            }
            toggleRegisterButtonState()
        }

        binding.etPassword.addTextChangedListener { text ->
            if (text.toString().length < 8 || !text.toString().matches(passwordRegex)) {
                binding.etPassword.setTextColor(ContextCompat.getColor(baseContext, R.color.red))
                isPasswordCorrect = false
            } else {
                binding.etPassword.setTextColor(ContextCompat.getColor(baseContext, R.color.dark_grey))
                isPasswordCorrect = true
            }
            toggleRegisterButtonState()
        }

        binding.etFullname.addTextChangedListener { text ->
            if (text.toString().length < 7 || text.toString().length > 20) {
                binding.etFullname.setTextColor(ContextCompat.getColor(baseContext, R.color.red))
                isFullNameCorrect = false
            } else {
                binding.etFullname.setTextColor(ContextCompat.getColor(baseContext, R.color.dark_grey))
                isFullNameCorrect = true
            }
            toggleRegisterButtonState()
        }

        binding.etEmail.addTextChangedListener { text ->
            if (!text.toString().matches(emailRegex)) {
                binding.etEmail.setTextColor(ContextCompat.getColor(baseContext, R.color.red))
                isEmailCorrect = false
            } else {
                binding.etEmail.setTextColor(ContextCompat.getColor(baseContext, R.color.dark_grey))
                isEmailCorrect = true
            }
            toggleRegisterButtonState()
        }
    }

    private fun toggleRegisterButtonState() {
        binding.btnRegister.isEnabled =
            isUsernameCorrect && isPasswordCorrect && isFullNameCorrect && isEmailCorrect

        if (binding.btnRegister.isEnabled)
            binding.btnRegister.background.setTint(ContextCompat.getColor(baseContext, R.color.green))
        else
            binding.btnRegister.background.setTint(ContextCompat.getColor(baseContext, R.color.light_grey))
    }

    private fun register(registerRequest: RegisterRequest) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.btnRegister.windowToken, 0)

        binding.progressRegister.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        binding.btnRegister.alpha = 0.5f


        val call: Call<UserResponse> = RetrofitInstance.userService.register(registerRequest)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    registrationSuccess(response.code(), response.body()!!)
                } else {
                    registrationError(response.code(), Exception("Registration failed: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                binding.progressRegister.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                binding.btnRegister.alpha = 1.0f
                registrationError(500, t)
            }
        })
    }

    private fun registrationSuccess(statusCode: Int, responseBody: UserResponse) {
        Constants.showSuccessSnackbar(
            this,
            layoutInflater,
            "Successful registration! Welcome ${responseBody.username}"
        )
        startActivity(
            Intent(this, LoginActivity::class.java)
                .putExtra("username", registerRequest.username)
                .putExtra("password", registerRequest.password)
        )
        finish()
    }

    private fun registrationError(statusCode: Int, e: Throwable) {
        val errorMessage = when (statusCode) {
            400 -> "User already exists"
            500 -> "Something unexpected happened"
            else -> "Error: $statusCode"
        }
        Constants.showErrorSnackbar(this, layoutInflater, errorMessage)
        e.printStackTrace()
    }
}
