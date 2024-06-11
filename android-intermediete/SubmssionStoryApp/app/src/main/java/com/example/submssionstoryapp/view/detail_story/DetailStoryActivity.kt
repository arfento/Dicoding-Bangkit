package com.example.submssionstoryapp.view.detail_story

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.data.model.ListStoryItem
import com.example.submssionstoryapp.databinding.ActivityDetailStoryBinding
import com.example.submssionstoryapp.utils.DateFormatter
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyItem = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY_ITEM)

        storyItem?.let { item ->
            detailView(item)
        }

    }

    private fun detailView(storyItem: ListStoryItem) {
        Glide.with(this@DetailStoryActivity)
            .load(storyItem.photoUrl)
            .into(binding.ivStory)

        binding.tvNama.text = storyItem.name
        binding.tvDesc.text = storyItem.description
        binding.tvLatitude.text = storyItem.lat.toString()
        binding.tvLongitude.text = storyItem.lat.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDate.text =
                DateFormatter.formatDate(storyItem.createdAt!!, TimeZone.getDefault().id)
        }
    }


    companion object {
        const val EXTRA_STORY_ITEM = "EXTRA_STORY_ITEM"
    }
}