package com.example.submssionstoryapp.data.api

import com.example.submssionstoryapp.data.model.FileUploadResponse
import com.example.submssionstoryapp.data.model.LoginResponse
import com.example.submssionstoryapp.data.model.RegisterResponse
import com.example.submssionstoryapp.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String, @Field("password") password: String
    ): LoginResponse

    // Post Story
    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse

    // Get Story
    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
    ): StoryResponse
}