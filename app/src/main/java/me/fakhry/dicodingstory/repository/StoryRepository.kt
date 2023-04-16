package me.fakhry.dicodingstory.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.data.StoryRemoteMediator
import me.fakhry.dicodingstory.database.StoryDatabase
import me.fakhry.dicodingstory.network.model.StoryItem
import me.fakhry.dicodingstory.network.retrofit.ApiService

class StoryRepository(
    private val pref: UserPreferences,
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    fun getStory(): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(pref, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}