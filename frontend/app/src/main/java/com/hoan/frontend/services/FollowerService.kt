package com.hoan.frontend.services

import com.hoan.frontend.models.dto.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FollowerService {

    @GET("followers/follow/{followed_id}")
    suspend fun follow(@Path("followed_id") followedId: Long): Response<MessageResponse>

    @GET("followers/unfollow/{followed_id}")
    suspend fun unfollow(@Path("followed_id") followedId: Long): Response<MessageResponse>
}
