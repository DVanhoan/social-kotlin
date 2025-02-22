package com.hoan.frontend.utils

import com.hoan.frontend.models.dto.request.LoginRequest
import com.hoan.frontend.models.dto.request.RegisterRequest
import com.hoan.frontend.models.dto.response.LoginResponse
import com.hoan.frontend.models.entities.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("v1/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @POST("v1/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("v1/users/refresh")
    suspend fun refreshToken(): Response<LoginResponse>

    @PUT("v1/users/password/{id}")
    suspend fun resetPassword(
        @Path("id") id: Int,
        @Body request: Map<String, String>
    ): Response<User>

    @GET("v1/users")
    suspend fun getAllUsers(): Response<List<User>>

    @DELETE("v1/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @GET("v1/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    @GET("v1/users/user/me")
    suspend fun getMe(): Response<User>

    @Multipart
    @POST("v1/users/pic")
    suspend fun uploadProfileImage(@Part image: retrofit2.http.Part): Response<User>


    @GET("v1/users/username/{username}")
    suspend fun getByUsername(@Path("username") username: String): Response<User>


    @PUT("v1/users/update")
    suspend fun updateUser(@Body updateRequest: Map<String, String>): Response<User>

    @GET("v1/users/logout")
    suspend fun logout(): Response<Unit>

}