package com.hoan.frontend.services

import com.hoan.frontend.models.dto.MessageResponse
import com.hoan.frontend.models.dto.auth.request.LoginRequest
import com.hoan.frontend.models.dto.auth.request.RefreshRequest
import com.hoan.frontend.models.dto.auth.request.RegisterRequest
import com.hoan.frontend.models.dto.auth.response.LoginResponse
import com.hoan.frontend.models.dto.auth.response.TokenResponse
import com.hoan.frontend.models.entities.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("users/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<MessageResponse>

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("users/refresh")
    suspend fun refresh(@Body refreshRequest: RefreshRequest): Response<TokenResponse>

    @GET("v1/users/user/me")
    suspend fun getMe(): Response<User>

    @GET("users/logout")
    suspend fun logout(): Response<MessageResponse>

    @GET("users/facebook")
    suspend fun facebookRedirect(): Response<Any>

    @GET("users/facebook/callback")
    suspend fun facebookCallback(): Response<Any>
}
