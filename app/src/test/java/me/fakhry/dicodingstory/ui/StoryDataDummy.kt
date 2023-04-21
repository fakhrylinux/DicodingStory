package me.fakhry.dicodingstory.ui

import me.fakhry.dicodingstory.network.model.StoryItem

object StoryDataDummy {

    fun generateDummyStoryList(): List<StoryItem> {
        val storyList = ArrayList<StoryItem>()
        for (i in 0..9) {
            val story = StoryItem(
                id = "story-$i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i.jpg",
                name = "Dummy Name $i",
                description = "Dummy Description $i"
            )
            storyList.add(story)
        }
        return storyList
    }
}