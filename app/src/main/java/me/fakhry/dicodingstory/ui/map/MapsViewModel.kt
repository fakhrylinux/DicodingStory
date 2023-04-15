package me.fakhry.dicodingstory.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.fakhry.dicodingstory.network.model.GetStoriesWithLocResponse
import me.fakhry.dicodingstory.network.model.StoryWithLoc
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private val _storyList = MutableLiveData<List<StoryWithLoc>>()
    val storyList: LiveData<List<StoryWithLoc>> = _storyList

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    fun getAllStoriesWithLocation(token: String) {
        val bearerToken = "Bearer $token"
        val service = ApiConfig.getApiServices().getAllStoriesWithLocation(bearerToken, 1)
        service.enqueue(object : Callback<GetStoriesWithLocResponse> {
            override fun onResponse(
                call: Call<GetStoriesWithLocResponse>,
                response: Response<GetStoriesWithLocResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody != null && !responseBody.error) {
                        _storyList.value = responseBody.listStory
                    }
                }
            }

            override fun onFailure(call: Call<GetStoriesWithLocResponse>, t: Throwable) {
                _responseMessage.value = "${t.message}"
            }
        })
    }
}