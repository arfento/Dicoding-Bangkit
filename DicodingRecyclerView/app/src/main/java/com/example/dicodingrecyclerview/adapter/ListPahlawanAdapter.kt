package com.example.dicodingrecyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingrecyclerview.R
import com.example.dicodingrecyclerview.models.Pahlawan

class ListPahlawanAdapter(private val listHero: ArrayList<Pahlawan>) :
    RecyclerView.Adapter<ListPahlawanAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto = itemView.findViewById<ImageView>(R.id.img_item_photo)
        val tvName = itemView.findViewById<TextView>(R.id.tv_item_name)
        val tvDescription = itemView.findViewById<TextView>(R.id.tv_item_description)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_pahlawan, parent, false);
        return ListViewHolder(view)

    }

    override fun getItemCount(): Int = listHero.size


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo) = listHero[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description

    }

}