package me.fakhry.dicodingstory.ui.map

import me.fakhry.dicodingstory.network.model.StoryWithLoc

object DataDummy {

    fun generateDummyStoryList(): List<StoryWithLoc> {
        val storyList = ArrayList<StoryWithLoc>()
        for (i in 0..9) {
            val story = StoryWithLoc(
                id = "story-$i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i.jpg",
                name = "Dummy Name $i",
                description = "Dummy Description $i",
                lat = -5.1568961,
                lon = 119.4381444
            )
            storyList.add(story)
        }
        return storyList
    }
}