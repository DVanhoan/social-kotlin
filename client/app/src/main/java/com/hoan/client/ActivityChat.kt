package com.hoan.client

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.adapter.MessagesRecycleViewAdapter
import com.hoan.client.databinding.ActivityChatBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.ConversationDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityChat : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"
    private lateinit var messagesAdapter: MessagesRecycleViewAdapter

    private var currentUserId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        currentUserId = sharedPreferences.getLong("userId", 0)

        setupRecyclerView()
        loadConversationDetail()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesRecycleViewAdapter(this, emptyList(), currentUserId)
        binding.recyclerViewMessages.adapter = messagesAdapter
    }

    private fun loadConversationDetail() {
        val conversationId = intent.getIntExtra("conversationId", 0)
        RetrofitInstance.messageService.getConversationDetail(conversationId)
            .enqueue(object : Callback<ConversationDetailResponse> {
                override fun onResponse(
                    call: Call<ConversationDetailResponse>,
                    response: Response<ConversationDetailResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val conversation = response.body()!!

                        messagesAdapter.updateMessages(conversation.messages)

                    }
                }
                override fun onFailure(call: Call<ConversationDetailResponse>, t: Throwable) {
                }
            })
    }
}
