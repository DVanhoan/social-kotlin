package com.hoan.client

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.adapter.ConversationRecyclerViewAdapter
import com.hoan.client.databinding.ActivityConversationBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.ConversationsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"
    private lateinit var conversationAdapter: ConversationRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        setupRecyclerView()
        getConversations()
    }

    private fun setupRecyclerView() {
        conversationAdapter = ConversationRecyclerViewAdapter(emptyList())
        binding.recyclerViewConversations.apply {
            layoutManager = LinearLayoutManager(this@ConversationActivity)
            adapter = conversationAdapter
        }
    }

    private fun getConversations() {
        RetrofitInstance.messageService.getConversations().enqueue(object : Callback<ConversationsResponse> {
                override fun onResponse(call: Call<ConversationsResponse>, response: Response<ConversationsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { conversationsResponse ->
                        val conversations = conversationsResponse.conversations
                        conversationAdapter.updateConversations(conversations)
                    } ?: run {
                        Log.e("ConversationActivity", "Response body null")
                        Toast.makeText(this@ConversationActivity, "Không có dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("ConversationActivity", "API error: ${response.message()}")
                    Toast.makeText(this@ConversationActivity, "Lỗi API: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConversationsResponse>, t: Throwable) {
                Log.e("ConversationActivity", "Failure: ${t.localizedMessage}")
                Toast.makeText(this@ConversationActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
