package me.fakhry.dicodingstory.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import me.fakhry.dicodingstory.network.model.StoryWithLoc
import me.fakhry.dicodingstory.repository.MapsRepository
import me.fakhry.dicodingstory.repository.Result
import me.fakhry.dicodingstory.ui.DataDummy
import me.fakhry.dicodingstory.ui.map.utils.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mapsRepository: MapsRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val storyList = DataDummy.generateDummyStoryList()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(mapsRepository)
    }

    @Test
    fun `when Get All Stories With Location Should Not Null`() {
        val expectedStory = MutableLiveData<Result<List<StoryWithLoc>>>()
        expectedStory.value = Result.Success(storyList)
        `when`(mapsRepository.getAllStoriesWithLocation("dummy_token", 1))
            .thenReturn(expectedStory)

        val actualStory = mapsViewModel.getAllStoriesWithLocation("dummy_token").getOrAwaitValue()
        Mockito.verify(mapsRepository).getAllStoriesWithLocation("dummy_token", 1)
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Result.Success)
        Assert.assertEquals(storyList.size, (actualStory as Result.Success).data.size)
        Assert.assertSame(storyList[0], actualStory.data[0])
    }

    @Test
    fun `when No Data Should Null and Return Error`() {
        val expectedStory = MutableLiveData<Result<List<StoryWithLoc>>>()
        expectedStory.value = Result.Error("Error")
        `when`(mapsRepository.getAllStoriesWithLocation("dummy_token", 1))
            .thenReturn(expectedStory)

        val actualStory = mapsViewModel.getAllStoriesWithLocation("dummy_token").getOrAwaitValue()
        Mockito.verify(mapsRepository).getAllStoriesWithLocation("dummy_token", 1)
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Result.Error)
    }
}