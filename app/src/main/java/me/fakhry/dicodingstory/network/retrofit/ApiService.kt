package me.fakhry.dicodingstory.network.retrofit

import me.fakhry.dicodingstory.network.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("register")
    fun registerRequest(
        @Body body: RegisterRequest
    ): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginRequest(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String
    ): Call<GetAllStoriesResponse>

    @Multipart
    @POST("/v1/stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<AddNewStoryResponse>
}