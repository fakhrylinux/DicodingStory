package me.fakhry.dicodingstory.ui

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.data.StoryPagingSource
import me.fakhry.dicodingstory.network.model.LoginRequest
import me.fakhry.dicodingstory.network.model.LoginResponse
import me.fakhry.dicodingstory.network.model.StoryItem
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSharedViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _respondMessage = MutableLiveData<String>()
    val respondMessage: LiveData<String> = _respondMessage

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyList = MutableLiveData<List<StoryItem>>()
    val storyList: LiveData<List<StoryItem>> = _storyList

    init {
        getAllStories()
    }

    fun getAllStories(): LiveData<PagingData<StoryItem>> {
        val apiService = ApiConfig.getApiServices()
        val pagingData = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, pref)
            }
        )

//        _storyList.value = pagingData.flow.map {
//            it.map {
//
//            }
//        }

        return pagingData.liveData
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearToken()
        }
    }

    fun loginRequest(email: String, password: String) {
        _isLoading.value = true
        val loginRequest = LoginRequest(email = email, password = password)
        val client = ApiConfig.getApiServices().loginRequest(loginRequest)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val token = responseBody.loginResult?.token
                        if (token != null) {
                            saveToken(token)
                        }
                    }
                } else {
                    _responseMessage.value = "Your email or password is incorrect"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d("viewModel", "${t.message}")
                _responseMessage.value = "${t.message}"
            }
        })
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun save(items: List<StoryItem>) {
        _storyList.value = items
    }
}