package com.example.snapstory

import com.example.snapstory.data.remote.model.StoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItem(
                i.toString(),
                "name $i",
                "photoUrl $i",
                "createdAt + $i",
                "description $i",
                0.0,
                0.0,
            )
            items.add(story)
        }
        return items
    }
}