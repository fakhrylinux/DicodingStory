package me.fakhry.dicodingstory.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.fakhry.dicodingstory.UserPreferences

class StoryViewModelFactory(private val pref: UserPreferences) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}