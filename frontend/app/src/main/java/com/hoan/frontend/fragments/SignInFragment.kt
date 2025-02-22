package com.hoan.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.hoan.frontend.R
import com.hoan.frontend.databinding.FragmentSigninBinding
import com.hoan.frontend.models.dto.auth.request.LoginRequest
import com.hoan.frontend.models.dto.auth.response.LoginResponse
import com.hoan.frontend.utils.Extensions.toast
import com.hoan.frontend.utils.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInFragment : Fragment(R.layout.fragment_signin) {

    private lateinit var binding: FragmentSigninBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSigninBinding.bind(view)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmailSignIn.text.toString().trim()
            val password = binding.etPasswordSignIn.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                requireActivity().toast("Some Fields are Empty")
            }
        }

        binding.tvNavigateToSignUp.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(usernameOrEmail = email, password = password)
                val response = RetrofitClient.authService.login(loginRequest)
                if (response.isSuccessful) {
                    val loginResponse: LoginResponse? = response.body()
                    val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("token", loginResponse?.token)
                        putString("user", Gson().toJson(loginResponse?.user))
                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        requireActivity().toast("Sign In Successful")
                        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                            .navigate(R.id.action_signInFragment_to_mainFragment)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        requireActivity().toast("Sign In Failed: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    requireActivity().toast("Error: ${e.localizedMessage}")
                }
            }
        }
    }
}
