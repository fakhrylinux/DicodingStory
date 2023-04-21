package me.fakhry.dicodingstory.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.network.model.StoryItem
import me.fakhry.dicodingstory.repository.StoryRepository

class StoryViewModel(
    private val pref: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStories(): LiveData<PagingData<StoryItem>> {
        return storyRepository.getStory().cachedIn(viewModelScope)
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearToken()
        }
    }
}