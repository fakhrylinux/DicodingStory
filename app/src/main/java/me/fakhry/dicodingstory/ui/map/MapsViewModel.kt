package me.fakhry.dicodingstory.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.fakhry.dicodingstory.network.model.StoryWithLoc
import me.fakhry.dicodingstory.repository.MapsRepository
import me.fakhry.dicodingstory.repository.Result

class MapsViewModel(private val mapsRepository: MapsRepository) : ViewModel() {

    fun getAllStoriesWithLocation(token: String): LiveData<Result<List<StoryWithLoc>>> =
        mapsRepository.getAllStoriesWithLocation(token, 1)
}