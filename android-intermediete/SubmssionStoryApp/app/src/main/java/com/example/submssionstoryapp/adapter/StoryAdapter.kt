package com.example.submssionstoryapp.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.data.model.ListStoryItem
import com.example.submssionstoryapp.databinding.ItemStoryBinding
import com.example.submssionstoryapp.utils.DateFormatter
import java.util.TimeZone

class StoryAdapter(
    private val listener: OnAdapterListener
) : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, listener)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("PrivateResource")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(stories: ListStoryItem, listener: OnAdapterListener) {
            binding.tvName.text = stories.name
            binding.tvDescription.text = stories.description
            binding.tvCreatedAt.text =
                DateFormatter.formatDate(stories.createdAt!!, TimeZone.getDefault().id)
            Glide.with(binding.root.context)
                .load(stories.photoUrl)
                .error(R.drawable.ic_baseline_preview_image_24)
                .into(binding.ivStory)

            binding.root.setOnClickListener {
                listener.onClick(stories)
            }        }
    }


//    inner class ViewHolder(private val binding: ItemStoryBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(stories: ListStoryItem) {
//
//        }
//    }


//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(storiesList[position])
//    }
//
//    override fun getItemCount(): Int {
//        return storiesList.size
//    }

    interface OnAdapterListener {
        fun onClick(story: ListStoryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}