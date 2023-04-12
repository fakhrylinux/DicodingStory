package me.fakhry.dicodingstory.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.fakhry.dicodingstory.network.model.RegisterRequest
import me.fakhry.dicodingstory.network.model.RegisterResponse
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(
        name: String,
        email: String,
        password: String,
    ) {
        _isLoading.value = true
        val registerRequest = RegisterRequest(name, email, password)
        val client = ApiConfig.getApiServices().registerRequest(registerRequest)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isError.value = false
                        _responseMessage.value = responseBody.message
                    }
                } else {
                    _isError.value = true
                    _responseMessage.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                _responseMessage.value = "${t.message}"
            }
        })
    }
}