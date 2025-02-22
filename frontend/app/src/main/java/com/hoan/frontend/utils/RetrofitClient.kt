package com.hoan.frontend.utils

import com.hoan.frontend.utils.MyApplication
import com.hoan.frontend.services.AuthService
import com.hoan.frontend.services.FollowerService
import com.hoan.frontend.services.PostService
import com.hoan.frontend.services.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.12:8000/api/v1/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(MyApplication.instance))
            .build()
    }


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val followerService: FollowerService by lazy {
        retrofit.create(FollowerService::class.java)
    }

    val postService: PostService by lazy {
        retrofit.create(PostService::class.java)
    }
}
