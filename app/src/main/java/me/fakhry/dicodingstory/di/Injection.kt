package me.fakhry.dicodingstory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.database.StoryDatabase
import me.fakhry.dicodingstory.network.retrofit.ApiConfig
import me.fakhry.dicodingstory.repository.MapsRepository
import me.fakhry.dicodingstory.repository.StoryRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

object Injection {
    fun provideRepository(): MapsRepository {
        val apiService = ApiConfig.getApiServices()
        return MapsRepository.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val storyDatabase = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiServices()
        val pref = UserPreferences.getInstance(context.dataStore)
        return StoryRepository(pref, storyDatabase, apiService)
    }
}