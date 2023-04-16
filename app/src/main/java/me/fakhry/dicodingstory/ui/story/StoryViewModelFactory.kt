package me.fakhry.dicodingstory.ui.story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.di.Injection
import me.fakhry.dicodingstory.ui.UserSharedViewModel

class StoryViewModelFactory(
    private val pref: UserPreferences,
    private val context: Context
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSharedViewModel::class.java)) {
            return UserSharedViewModel(pref, Injection.provideStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}