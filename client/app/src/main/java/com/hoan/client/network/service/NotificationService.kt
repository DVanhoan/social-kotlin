package com.hoan.client.network.service

import com.hoan.client.network.response.Notification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface NotificationService {
    @GET("notifications")
    suspend fun getAll(): Response<List<Notification>>

    @POST("notifications/mark-as-read")
    suspend fun markAsRead(
        @Query("notificationId") notificationId: Long
    ): Response<Unit>
}