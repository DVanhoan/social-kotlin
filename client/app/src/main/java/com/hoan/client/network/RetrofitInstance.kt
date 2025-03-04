package com.hoan.client.network

import com.hoan.client.network.service.CommentService
import com.hoan.client.network.service.FriendService
import com.hoan.client.network.service.PostService
import com.hoan.client.network.service.ReactionService
import com.hoan.client.network.service.UserService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    private var token: String = ""

    fun setToken(newToken: String) {
        token = newToken
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        })
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("http://192.168.1.14:8000/api/v1/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
    val postService: PostService = retrofit.create(PostService::class.java)
    val reactionService: ReactionService = retrofit.create(ReactionService::class.java)
    val commentService: CommentService = retrofit.create(CommentService::class.java)
    val friendService: FriendService = retrofit.create(FriendService::class.java)
}
