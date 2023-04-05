package me.fakhry.dicodingstory.network.retrofit

import me.fakhry.dicodingstory.network.model.LoginRequest
import me.fakhry.dicodingstory.network.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login")
    fun loginRequest(
        @Body body: LoginRequest
    ): Call<LoginResponse>
}