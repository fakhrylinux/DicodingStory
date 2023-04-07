package me.fakhry.dicodingstory.ui

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.network.model.GetAllStoriesResponse
import me.fakhry.dicodingstory.network.model.ListStoryItem
import me.fakhry.dicodingstory.network.model.LoginRequest
import me.fakhry.dicodingstory.network.model.LoginResponse
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSharedViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val token: Flow<String> = pref.getToken()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _respondMessage = MutableLiveData<String>()
    val respondMessage: LiveData<String> = _respondMessage

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private val _isFormValid = MutableLiveData<Boolean>()
    val isFormValid: LiveData<Boolean> = _isFormValid

    fun getAllStories(token: String) {
        val client = ApiConfig.getApiServices().getAllStories("Bearer $token")
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>,
                response: Response<GetAllStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listStories.value = responseBody.listStory
                    } else {
                        _isError.value = true
                        _respondMessage.value = "No Story Found"
                    }
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                _respondMessage.value = "${t.message}"
            }
        })
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearToken()
            _isLoggedIn.value = false
        }
    }

    fun loginRequest(email: String, password: String) {
        _isLoading.value = true
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
                        _responseMessage.value = responseBody?.message
                        val token = responseBody?.loginResult?.token
                        if (token != null) {
                            saveToken(token)
                        }
                        _isLoginSuccess.value = true
                    }
                } else {
                    _isLoginSuccess.value = false
                    _responseMessage.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isLoginSuccess.value = false
                _isFormValid.value = true
                _responseMessage.value = "${t.message}"
            }
        })
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getResponseMessage(): String? {
        return responseMessage.value
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}