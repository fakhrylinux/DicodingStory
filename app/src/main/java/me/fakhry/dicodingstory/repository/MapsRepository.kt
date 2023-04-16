package me.fakhry.dicodingstory.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import me.fakhry.dicodingstory.network.model.StoryWithLoc
import me.fakhry.dicodingstory.network.retrofit.ApiService

class MapsRepository(private val apiService: ApiService) {

    fun getAllStoriesWithLocation(
        token: String,
        location: Int
    ): LiveData<Result<List<StoryWithLoc>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStoriesWithLocation(token, location)
            emit(Result.Success(response.listStory))

        } catch (e: Exception) {
            Log.d("MapsRepository", "getAllStoriesWithLocation: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: MapsRepository? = null
        fun getInstance(
            apiService: ApiService
        ): MapsRepository =
            instance ?: synchronized(this) {
                instance ?: MapsRepository(apiService)
            }.also { instance = it }
    }
}