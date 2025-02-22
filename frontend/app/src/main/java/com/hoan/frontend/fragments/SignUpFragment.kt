package com.hoan.frontend.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.hoan.frontend.R
import com.hoan.frontend.databinding.FragmentSignupBinding
import com.hoan.frontend.models.dto.request.RegisterRequest
import com.hoan.frontend.utils.RetrofitClient
import com.hoan.frontend.utils.Extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)

        binding.btnSignUp.setOnClickListener {
            val name = binding.etNameSignUp.text.toString().trim()
            val username = binding.etUserNameSignUp.text.toString().trim()  // EditText cho username
            val email = binding.etEmailSignUp.text.toString().trim()
            val password = binding.etPasswordSignUp.text.toString().trim()

            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, username, email, password)
            } else {
                requireActivity().toast("Vui lòng điền đầy đủ thông tin")
            }
        }

        binding.tvNavigateToSignIn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }

    private fun registerUser(name: String, username: String, email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(
                    name = name,
                    username = username,
                    email = email,
                    password = password
                )
                val response = RetrofitClient.apiService.register(registerRequest)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        requireActivity().toast("Đăng ký thành công")
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_signUpFragment_to_mainFragment)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        requireActivity().toast("Đăng ký thất bại: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    requireActivity().toast("Lỗi: ${e.localizedMessage}")
                }
            }
        }
    }
}
