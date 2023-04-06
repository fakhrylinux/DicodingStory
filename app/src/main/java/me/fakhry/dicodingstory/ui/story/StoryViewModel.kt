package me.fakhry.dicodingstory.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.network.model.GetAllStoriesResponse
import me.fakhry.dicodingstory.network.model.ListStoryItem
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val token: Flow<String> = pref.getToken()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _respondMessage = MutableLiveData<String>()
    val respondMessage: LiveData<String> = _respondMessage

    init {
        viewModelScope.launch {
            token.collect { value ->
                getAllStories(value)
            }
        }
    }

    private fun getAllStories(token: String) {
        val client = ApiConfig.getApiServices().getAllStories("Bearer $token")
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>,
                response: Response<GetAllStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if ((responseBody != null) && responseBody.listStory.isEmpty()) {
                        _isError.value = true
                        _respondMessage.value = "No Story Found"
                    }
                    _listStories.value = responseBody?.listStory
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                _respondMessage.value = "${t.message}"
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    suspend fun clearToken() {
        pref.clearToken()
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}