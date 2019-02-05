package com.kimcy929.iconpakagereader.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimcy929.iconpakagereader.R
import com.kimcy929.iconpakagereader.iconhelper.IconPackInfo

/**
 * Created by Kimcy929 on 07/11/2016.
 */
class IconPackNameAdapter(
    context: Context,
    private val listIconPack: List<IconPackInfo>,
    private val clickListener: ClickListener
) : RecyclerView.Adapter<IconPackNameAdapter.ViewHolder>() {

    private val iconSize: Int = context.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_pack_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val iconPack = listIconPack[position]
        holder.txtIconPackName.text = iconPack.label
        val drawable = iconPack.icon
        if (drawable != null) {
            drawable.setBounds(0, 0, iconSize, iconSize)
            holder.txtIconPackName.setCompoundDrawables(drawable, null, null, null)
        }
    }

    override fun getItemCount(): Int {
        return listIconPack.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtIconPackName: TextView = itemView.findViewById(R.id.txtIconPackName)

        init {
            itemView.setOnClickListener { clickListener.onClick(adapterPosition) }
        }
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

}
