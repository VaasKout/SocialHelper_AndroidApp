package com.example.socialhelper.socialworker

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialhelper.R
import com.example.socialhelper.database.WheelData


class SocialAdapter:
    RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

    var data = listOf<WheelData>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SocialAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.comment.text = item.comment
        holder.name.text = item.name
        holder.firstStation.text = item.first
        holder.secondStation.text = item.second
        holder.time.text = item.time
        holder.button.setOnClickListener {
            item.checked = true
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.titleRecyclerItem)
        val firstStation: TextView = itemView.findViewById(R.id.recyclerFirstStation)
        val secondStation: TextView = itemView.findViewById(R.id.recyclerSecondStation)
        val comment: TextView = itemView.findViewById(R.id.recyclerComment)
        val time: TextView = itemView.findViewById(R.id.recyclerTime)
        val button: Button = itemView.findViewById(R.id.recyclerButton)
    }
}

class Social(val clickListener: (data: WheelData) -> Unit) {
    fun onClick(data: WheelData) = clickListener(data)
}
