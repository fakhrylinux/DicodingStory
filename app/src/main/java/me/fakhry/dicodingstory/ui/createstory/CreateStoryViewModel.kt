package me.fakhry.dicodingstory.ui.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.fakhry.dicodingstory.network.model.AddNewStoryResponse
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateStoryViewModel : ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    fun addStoryRequest(photo: MultipartBody.Part, description: RequestBody, token: String) {
        _isLoading.value = true
        val bearerToken = "Bearer $token"
        val service = ApiConfig.getApiServices().addNewStory(photo, description, bearerToken)
        service.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody != null && !responseBody.error) {
                        _isSuccess.value = true
                    }
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _responseMessage.value = "${t.message}"
                _isSuccess.value = false
            }
        })
    }
}