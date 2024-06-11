package com.example.submssionstoryapp

import com.example.submssionstoryapp.data.model.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl $i",
                "name $i",
                "description $i",
                0.4,
                0.4,
                "1",
                "createdAt + $i",
            )
            items.add(story)
        }
        return items
    }
}