package com.hoan.client.network.service

import com.hoan.client.network.response.FriendshipResponse
import retrofit2.Call
import retrofit2.http.*

interface FriendService {

    @POST("friendlist/add/{userId}")
    fun addFriend(@Path("userId") userId: Long): Call<FriendshipResponse>

    @PATCH("friendlist/accept/{userId}")
    fun acceptFriendRequest(@Path("userId") userId: Long): Call<FriendshipResponse>

    @PATCH("friendlist/reject/{userId}")
    fun rejectFriend(@Path("userId") userId: Long): Call<Boolean>

    @GET("friendlist/pending")
    fun getPendingRequests(): Call<List<FriendshipResponse>>

    @GET("friendlist/friends")
    fun getListOfFriends(): Call<List<Long>>

    @GET("friendlist/sent")
    fun getSentRequests(): Call<List<FriendshipResponse>>
}
