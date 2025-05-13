package com.hoan.client.viewmodel

import androidx.lifecycle.*
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.Notification
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<Notification>>(emptyList())
    val notifications: LiveData<List<Notification>> = _notifications

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val resp = RetrofitInstance.notificationService.getAll()
                if (resp.isSuccessful) {
                    _notifications.value = resp.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Lỗi API: ${resp.code()} ${resp.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Không thể kết nối: ${e.localizedMessage}"
            }
        }
    }

    fun markAsRead(notificationId: Long, onComplete: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            val resp = RetrofitInstance.notificationService.markAsRead(notificationId)
            onComplete(resp.isSuccessful)
            if (!resp.isSuccessful) {
                _error.value = "Đánh dấu đọc thất bại: ${resp.code()}"
            }
        }
    }
}
