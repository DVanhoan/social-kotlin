package com.hoan.client

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoan.client.databinding.ActivityMessageBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.UserResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var chat :ChatResponse
    private val sharedPrefName = "user_shared_preference"

    private val picasso: Picasso by lazy { Picasso.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""

        RetrofitInstance.setToken(token)
    }

    private fun getMessages() {
        RetrofitInstance.userService.getUser()
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getUserSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error retrieving user: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

}