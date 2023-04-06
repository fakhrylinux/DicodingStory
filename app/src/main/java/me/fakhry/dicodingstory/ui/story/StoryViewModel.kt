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
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _listStories.value = response.body()!!.listStory
                    }
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}