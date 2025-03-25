package com.hoan.client.network.service

import com.hoan.client.network.request.JwtRequest
import com.hoan.client.network.request.RegisterRequest
import com.hoan.client.network.request.UserRequest
import com.hoan.client.network.response.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
interface UserService {

    @POST("users/login")
    fun login(@Body jwtRequest: JwtRequest): Call<JwtResponse>

    @POST("users/register")
    fun register(@Body userRequest: RegisterRequest): Call<UserResponse>

    @GET("users/user/me")
    fun getUser(): Call<UserResponse>

    @POST("users/user")
    fun editUser(@Body userRequest: UserRequest): Call<UserResponse>

    @GET("users/user/list")
    fun loadUserList(): Call<List<UserResponse>>

    @GET("users/user/user-by-userId/{userId}")
    fun getUserByUserId(@Path("userId") userId: Long): Call<UserResponse>

    @Multipart
    @POST("users/user/upload-profile-picture")
    fun uploadProfilePicture(@Part picture: MultipartBody.Part): Call<UserResponse>

    @GET("users/user/profile-picture/{userId}")
    fun getProfilePictureUrl(@Path("userId") userId: Long): Call<ResponseBody>
}
