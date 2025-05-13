// ChatViewModel.kt
package com.hoan.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.ConversationsResponse
import com.hoan.client.network.response.ConversationItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {
    private val _conversations = MutableLiveData<List<ConversationItem>>(emptyList())
    val conversations: LiveData<List<ConversationItem>> = _conversations

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun fetchConversations() {
        RetrofitInstance.messageService.getConversations()
            .enqueue(object : Callback<ConversationsResponse> {
                override fun onResponse(
                    call: Call<ConversationsResponse>,
                    response: Response<ConversationsResponse>
                ) {
                    if (response.isSuccessful) {
                        _conversations.value = response.body()?.conversations ?: emptyList()
                        _error.value = null
                    } else {
                        _error.value = "Lỗi API: ${response.message()}"
                    }
                }
                override fun onFailure(call: Call<ConversationsResponse>, t: Throwable) {
                    _error.value = "Không thể kết nối: ${t.localizedMessage}"
                }
            })
    }
}
