package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

data class InfoBoxData(val title: String, val content: String)

class InfoBoxAdapter(private val infoList: List<InfoBoxData>) : RecyclerView.Adapter<InfoBoxAdapter.InfoBoxViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoBoxViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_info_box, parent, false)
        return InfoBoxViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoBoxViewHolder, position: Int) {
        val infoBoxData = infoList[position]
        holder.title.text = infoBoxData.title
        holder.content.text = infoBoxData.content
    }

    override fun getItemCount(): Int = infoList.size

    inner class InfoBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.infoTitle)
        val content: TextView = itemView.findViewById(R.id.infoContent)
    }
}