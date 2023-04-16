package me.fakhry.dicodingstory.network.model

import com.google.gson.annotations.SerializedName

data class GetStoriesWithLocResponse(

    @field:SerializedName("listStory")
    val listStory: List<StoryWithLoc>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class StoryWithLoc(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Double
)
