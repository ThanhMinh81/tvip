package com.example.iptvplayer.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iptvplayer.R
import com.example.iptvplayer.model.Category

class CategoryAdapter(
    private val items: ArrayList<Category>,
    private val context: Context,
    private val onItemClick: (String,Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {

    var itemChecked: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ItemViewHolder(view)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCategory)
        val layoutRoot: LinearLayout = itemView.findViewById(R.id.layout_root_category)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.setText(item.name)

        if (itemChecked == item.id) {
            holder.layoutRoot.setBackgroundResource(R.drawable.tab_check)
            holder.tvName.setTextColor(context.resources.getColor(R.color.black, null))
        } else {
            holder.layoutRoot.setBackgroundResource(R.drawable.tab_uncheck)
            holder.tvName.setTextColor(context.resources.getColor(R.color.bg_category_check, null))
        }

        holder.layoutRoot.setOnClickListener {
            onItemClick(item.name,item.id)
        }

    }

    override fun getItemCount() = items.size

    fun updateItemChecked(value: Int) {
        itemChecked = value
    }

}