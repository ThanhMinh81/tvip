package com.example.iptvplayer.home.adapter

import android.content.Context
import android.util.Log
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


class ItemChannelAdapter(
    private val items: ArrayList<ItemChannel?>,
    private val context: Context,
    private val onItemClick: (ItemChannel, index: Int) -> Unit
) : RecyclerView.Adapter<ItemChannelAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_channel_layout, parent, false)
        return ItemViewHolder(view)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ShapeableImageView = itemView.findViewById(R.id.img_channel)
        val tvName: TextView = itemView.findViewById(R.id.tvNameChannel)
        val root: LinearLayout = itemView.findViewById(R.id.layout_root)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        item?.let {
            holder.tvName.setText(item.channelName)
            Glide
                .with(context)
                .load(item.logoUrl)
                .centerInside()
                .into(holder.logo);
            holder.root.setOnClickListener {

                onItemClick(item, position)
            }

            if (item.selected == 0)
                holder.tvName.setTextColor(context.resources.getColor(R.color.white, null))
            else
                holder.tvName.setTextColor(
                    context.resources.getColor(
                        R.color.bg_category_check,
                        null
                    )
                )
        }

    }

    override fun getItemCount() = items.size

    fun itemSelected(idItem: Int) {

        items.forEach { it ->
            if (it != null) {
                if (it.selected > 0) it.selected = 0
            }
        }

        val indexSelected = items.indexOfFirst { it -> it?.id == idItem }
        if (indexSelected != -1) {
            Log.d("523534525235", idItem.toString())
            items[indexSelected]?.selected = 1

        }
        notifyDataSetChanged()

    }

}
