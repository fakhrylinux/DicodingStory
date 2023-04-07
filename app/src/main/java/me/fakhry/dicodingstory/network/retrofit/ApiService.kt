package me.fakhry.dicodingstory.network.retrofit

import me.fakhry.dicodingstory.network.model.GetAllStoriesResponse
import me.fakhry.dicodingstory.network.model.LoginRequest
import me.fakhry.dicodingstory.network.model.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginRequest(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String
    ): Call<GetAllStoriesResponse>
}