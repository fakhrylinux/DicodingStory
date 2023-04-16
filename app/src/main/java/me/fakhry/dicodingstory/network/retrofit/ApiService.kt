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
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesResponse

//    @GET("stories")
//    fun getAllStoriesWithLocation(
//        @Header("Authorization") token: String,
//        @Query("location") location: Int
//    ): Call<GetStoriesWithLocResponse>

    @GET("stories")
    suspend fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): GetStoriesWithLocResponse

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<AddNewStoryResponse>
}