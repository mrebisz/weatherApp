package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class SearchResultsAdapter(
    private var locations: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        holder.locationName.text = location
        holder.itemView.setOnClickListener {
            onItemClick(location)
        }
    }

    override fun getItemCount(): Int = locations.size

    fun updateList(newLocations: List<String>) {
        locations = newLocations
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName: TextView = itemView.findViewById(R.id.locationName)
    }
}