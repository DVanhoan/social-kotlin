package com.hoan.frontend.services

import com.hoan.frontend.models.dto.MessageResponse
import com.hoan.frontend.models.dto.user.PasswordRequest
import com.hoan.frontend.models.dto.user.UpdateRequest
import com.hoan.frontend.models.entities.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<MessageResponse>

    @PUT("users/password/{id}")
    suspend fun resetPassword(
        @Path("id") id: Long,
        @Body passwordRequest: PasswordRequest
    ): Response<MessageResponse>

    @Multipart
    @POST("users/pic")
    suspend fun uploadProfileImage(
        @Part pic: MultipartBody.Part
    ): Response<MessageResponse>

    @GET("users/id/{id}")
    suspend fun getById(@Path("id") id: Long): Response<User>

    @GET("users/username/{username}")
    suspend fun getByUsername(@Path("username") username: String): Response<User>

    @PUT("users/update")
    suspend fun updateUser(@Body updateRequest: UpdateRequest): Response<User>
}
