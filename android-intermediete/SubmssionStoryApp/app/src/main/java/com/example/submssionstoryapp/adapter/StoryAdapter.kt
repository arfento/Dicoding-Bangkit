package com.example.submssionstoryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.data.model.ListStoryItem
import com.example.submssionstoryapp.databinding.ItemStoryBinding
import com.example.submssionstoryapp.utils.DateFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StoryAdapter(
    private val storiesList: List<ListStoryItem>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stories: ListStoryItem) {
            binding.tvName.text = stories.name
            binding.tvDescription.text = stories.description
            binding.tvCreatedAt.text = DateFormatter.formatDate(stories.createdAt!!, TimeZone.getDefault().id)
            Glide.with(binding.root.context)
                .load(stories.photoUrl)
                .error(com.google.android.material.R.drawable.mtrl_ic_error)
                .into(binding.ivStory)

            binding.root.setOnClickListener {
                listener.onClick(stories)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(storiesList[position])


    }

    override fun getItemCount(): Int {
        return storiesList.size
    }

    interface OnAdapterListener {
        fun onClick(story: ListStoryItem)
    }


}