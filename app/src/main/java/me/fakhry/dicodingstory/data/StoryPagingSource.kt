package me.fakhry.dicodingstory.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.first
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.network.model.StoryItem
import me.fakhry.dicodingstory.network.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService, private val pref: UserPreferences) :
    PagingSource<Int, StoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val tokenAuth = "Bearer ${pref.getToken().first()}"
            val responseData = apiService.getAllStories(tokenAuth, position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}