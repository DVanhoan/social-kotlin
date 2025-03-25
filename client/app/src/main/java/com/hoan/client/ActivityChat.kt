package com.hoan.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoan.client.databinding.ActivityChatBinding

class ActivityChat: AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getChat() {
        // get chat from server
    }
}