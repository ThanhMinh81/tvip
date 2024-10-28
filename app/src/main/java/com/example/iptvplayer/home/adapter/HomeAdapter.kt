package com.example.iptvplayer.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iptvplayer.R
import com.example.iptvplayer.model.ItemChannel
import com.google.android.material.imageview.ShapeableImageView


class HomeAdapter(
    private val items: ArrayList<ItemChannel>,
    private val context: Context,
    private val onItemClick: (ItemChannel) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ItemViewHolder>() {

    var category: String = "all"
    var listBackup : ArrayList<ItemChannel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ShapeableImageView = itemView.findViewById(R.id.img_logo)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val heart: ImageView = itemView.findViewById(R.id.ic_heart)
        val layoutRoot: LinearLayout = itemView.findViewById(R.id.layoutParent)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.setText(item.channelName)

        Glide
            .with(context)
            .load(item.logoUrl)
            .centerInside()
            .into(holder.logo);

        holder.layoutRoot.setOnClickListener {
            onItemClick(item)
        }

    }

    override fun getItemCount() = items.size

    fun setListBackUp(value: ArrayList<ItemChannel>){
        this.listBackup = value
    }

    fun filterCategory(value: String = "all") {
        category = value
        items.clear()

        var listDisplay : ArrayList<ItemChannel> = arrayListOf()

        if(category != "all"){
            val filteredItems = listBackup.filter { it.category.equals(category) }
            listDisplay.addAll(filteredItems)
            items.addAll(listDisplay)
        }else{
            items.addAll(listBackup)
        }
        notifyDataSetChanged()

    }
}