package me.fakhry.dicodingstory.ui.auth.login

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.network.model.LoginRequest
import me.fakhry.dicodingstory.network.response.LoginResponse
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private val _isFormValid = MutableLiveData<Boolean>()
    val isFormValid: LiveData<Boolean> = _isFormValid

    fun loginRequest(email: String, password: String) {
        _isLoading.value = true
        _isLoginSuccess.value = false
        val loginRequest = LoginRequest(email = email, password = password)
        val client = ApiConfig.getApiServices().loginRequest(loginRequest)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                _isFormValid.value = true
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.error == true) {
                        _isLoginSuccess.value = false
                        _responseMessage.value = responseBody.message
                    } else {
                        val token = response.body()?.loginResult?.token
                        if (token != null) {
                            saveToken(token)
                        }
                        _isLoginSuccess.value = true
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isFormValid.value = true
                _responseMessage.value = "${t.message}"
                Log.e("LoginFragment", "onFailure: ${t.message}")
            }
        })
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun isFormValid(email: String, password: String): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isFormValid.value = false
            return true
        }
        _isFormValid.value = true
        return false
    }
}