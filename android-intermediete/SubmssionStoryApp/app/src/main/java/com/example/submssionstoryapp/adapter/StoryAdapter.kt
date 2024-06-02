package com.example.submssionstoryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submssionstoryapp.R
import com.example.submssionstoryapp.data.model.ListStoryItem
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter(
    private val storiesList: List<ListStoryItem>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvName)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val createdAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val photo: ImageView = itemView.findViewById(R.id.ivStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = storiesList[position]
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(item.createdAt)
        val formattedDatesString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        holder.name.text = item.name
        holder.description.text = item.description
        holder.createdAt.text = formattedDatesString
        Glide.with(holder.itemView.context)
            .load(item.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .into(holder.photo)

        holder.itemView.setOnClickListener {
            listener.onClick(item)
        }
    }

    override fun getItemCount(): Int {
        return storiesList.size
    }

    interface OnAdapterListener {
        fun onClick(story: ListStoryItem)
    }
}